package org.hzero.message.app.service.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.hzero.boot.message.entity.MessageSender;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.boot.message.entity.WebHookSender;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.convert.CommonConverter;
import org.hzero.core.util.SystemClock;
import org.hzero.message.api.dto.UserMessageDTO;
import org.hzero.message.app.service.MessageGeneratorService;
import org.hzero.message.app.service.MessageReceiverService;
import org.hzero.message.app.service.WebHookSendService;
import org.hzero.message.config.MessageConfigProperties;
import org.hzero.message.domain.entity.Message;
import org.hzero.message.domain.entity.MessageReceiver;
import org.hzero.message.domain.entity.MessageTransaction;
import org.hzero.message.domain.repository.MessageReceiverRepository;
import org.hzero.message.domain.repository.MessageRepository;
import org.hzero.message.domain.repository.MessageTransactionRepository;
import org.hzero.message.domain.vo.*;
import org.hzero.message.infra.constant.HmsgConstant;
import org.hzero.message.infra.retry.MessageSendRetryer;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.exception.CommonException;

/**
 * webHook消息发送
 * 覆盖该方法 不能因为一个消息出错 导致后续都发送不成功
 * {@link WebHookSendServiceImpl#sendWebHook(org.hzero.boot.message.entity.WebHookSender, java.lang.Integer)}
 * @author xiaoyu.zhao@hand-china.com 2020-04-26 19:57:46
 */
@Service
@DubboService
public class WebHookSendServiceImpl extends AbstractSendService implements WebHookSendService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebHookSendServiceImpl.class);
    private static final String URL_REGEX = "(http[s]{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?([a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>[\\u4E00-\\u9FA5\\uF900-\\uFA2D]*]*)+";
    private static final String CHINESE_REGEX = "[\\u4E00-\\u9FA5\\uF900-\\uFA2D]+";

    private final MessageRepository messageRepository;
    private final MessageReceiverService messageReceiverService;
    private final MessageConfigProperties messageConfigProperties;
    private final MessageGeneratorService messageGeneratorService;
    private final MessageTransactionRepository messageTransactionRepository;
    private final MessageReceiverRepository messageReceiverRepository;
    private final MessageSendRetryer messageSendRetryer;

    @Autowired
    public WebHookSendServiceImpl(MessageRepository messageRepository,
                                  MessageReceiverService messageReceiverService,
                                  MessageConfigProperties messageConfigProperties,
                                  MessageGeneratorService messageGeneratorService,
                                  MessageTransactionRepository messageTransactionRepository,
                                  MessageReceiverRepository messageReceiverRepository,
                                  MessageSendRetryer messageSendRetryer) {
        this.messageRepository = messageRepository;
        this.messageReceiverService = messageReceiverService;
        this.messageConfigProperties = messageConfigProperties;
        this.messageGeneratorService = messageGeneratorService;
        this.messageTransactionRepository = messageTransactionRepository;
        this.messageReceiverRepository = messageReceiverRepository;
        this.messageSendRetryer = messageSendRetryer;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message sendWebHookMessage(Long organizationId, WebHookSender messageSender) {
        messageSender.setTenantId(organizationId);
        return sendMessage(messageSender, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message sendMessage(WebHookSender messageSender, Integer tryTimes) {
        if (messageConfigProperties.isAsync()) {
            ApplicationContextHelper.getContext().getBean(WebHookSendService.class).asyncSendMessage(messageSender, tryTimes);
            return null;
        } else {
            return sendWebHook(messageSender, tryTimes);
        }
    }

    @Override
    @Async("commonAsyncTaskExecutor")
    public void asyncSendMessage(WebHookSender messageSender, Integer tryTimes) {
        sendWebHook(messageSender, tryTimes);
    }

    private Message sendWebHook(WebHookSender messageSender, Integer tryTimes) {
        Assert.notNull(messageSender.getMessageCode(), BaseConstants.ErrorCode.DATA_INVALID);
        Assert.notNull(messageSender.getServerCode(), BaseConstants.ErrorCode.DATA_INVALID);
        Assert.notNull(messageSender.getTenantId(), BaseConstants.ErrorCode.DATA_INVALID);
        // 获取webHook配置
        WebhookServerConfig webhookServer = WebhookServerConfig.getConfig(messageSender.getTenantId(), messageSender.getServerCode());
        // 确保WebHook一定存在
        // todo 覆盖逻辑
//        Assert.notNull(webhookServer, HmsgConstant.ErrorCode.WEBHOOK_NOT_EXISTS);
        if (webhookServer == null) {
            return null;
        }
        if (StringUtils.isNotBlank(webhookServer.getSecret())) {
            webhookServer.setSecret(this.decryptSecret(webhookServer.getSecret()));
        }
        // 生成消息记录
        Message message = createMessage(messageSender, HmsgConstant.MessageType.WEB_HOOK);
        try {
            // 获取消息内容
            message = messageGeneratorService.generateMessage(messageSender, message);
            // 获取消息接收人
            MessageSender ms = messageReceiverService
                    .queryReceiver(new MessageSender().setTenantId(messageSender.getTenantId())
                            .setReceiverTypeCode(messageSender.getReceiverTypeCode())
                            .setReceiverAddressList(messageSender.getReceiverAddressList()));
            // 组装数据
            messageSender.setMessage(CommonConverter.beanConvert(org.hzero.boot.message.entity.Message.class, message))
                    .setWebhookAddress(webhookServer.getWebhookAddress())
                    .setSecret(webhookServer.getSecret())
                    .setServerType(webhookServer.getServerType())
                    .setReceiverAddressList(ms.getReceiverAddressList())
                    .setServerCode(webhookServer.getServerCode());
            // 发送消息
            messageRepository.updateByPrimaryKeySelective(message);
            Long messageId = message.getMessageId();
            Long tenantId = message.getTenantId();
            // 记录接收人信息
            if (CollectionUtils.isNotEmpty(ms.getReceiverAddressList())
                    && StringUtils.equalsAny(messageSender.getServerType(), HmsgConstant.WebHookServerType.DING_TALK, HmsgConstant.WebHookServerType.WE_CHAT)) {
                ms.getReceiverAddressList().forEach(receiver -> {
                    if (!StringUtils.isBlank(receiver.getPhone())) {
                        messageReceiverRepository.insertSelective(new MessageReceiver()
                                .setMessageId(messageId)
                                .setTenantId(tenantId)
                                .setIdd(receiver.getIdd())
                                .setReceiverAddress(receiver.getPhone())
                        );
                    }
                });
            }
            sendWHMessage(messageSender, message, tryTimes);
            // 记录成功
            messageRepository.updateByPrimaryKeySelective(message.setSendFlag(BaseConstants.Flag.YES));
            MessageTransaction transaction = new MessageTransaction().setMessageId(message.getMessageId())
                    .setTrxStatusCode(HmsgConstant.TransactionStatus.S)
                    .setTenantId(messageSender.getTenantId());
            messageTransactionRepository.insertSelective(transaction);
            message.setTransactionId(transaction.getTransactionId());
        } catch (Exception e) {
            // 记录失败
            failedProcess(message, e);
        }
        return message;
    }

    /**
     * 发送webhook消息
     *
     * @param messageSender WebHookSender
     * @param message       消息内容类
     * @param tryTimes      重试次数
     */
    private void sendWHMessage(WebHookSender messageSender, Message message, Integer tryTimes) throws ExecutionException, RetryException {
        // 获取接收人
        List<Receiver> receivers = messageSender.getReceiverAddressList();
        Set<String> phoneSet = new HashSet<>();
        // 判断WebHook的类型，执行不同的消息发送方式
        switch (messageSender.getServerType()) {
            case HmsgConstant.WebHookServerType.JSON:
                sendWHJsonMessage(messageSender, message, tryTimes);
                break;
            case HmsgConstant.WebHookServerType.DING_TALK:
                // 获取手机号信息，用于钉钉@人使用
                if (CollectionUtils.isNotEmpty(receivers)) {
                    for (Receiver receiver : receivers) {
                        if (StringUtils.isBlank(receiver.getPhone())) {
                            continue;
                        }
                        // 处理消息发送配置发送webhook消息传递手机号中使用逗号拼接多个手机号的情况
                        String[] split = StringUtils.split(receiver.getPhone(), BaseConstants.Symbol.COMMA);
                        for (String phone : split) {
                            // 拼接国际冠码
                            if (StringUtils.isNotBlank(receiver.getIdd())) {
                                phoneSet.add(StringUtils.join(receiver.getIdd(), BaseConstants.Symbol.MIDDLE_LINE, phone));
                            } else {
                                phoneSet.add(phone);
                            }
                        }
                    }
                }
                switch (messageSender.getMsgType()) {
                    case TEXT:
                        sendWhDingTalkTextMessage(messageSender, message, phoneSet, tryTimes);
                        break;
                    case MARK_DOWN:
                    default:
                        sendWhDingTalkMarkdownMessage(messageSender, message, phoneSet, tryTimes);
                        break;
                }
                break;
            case HmsgConstant.WebHookServerType.WE_CHAT:
                switch (messageSender.getMsgType()) {
                    case TEXT:
                        // 获取手机号信息，用于企业微信@人使用  企业微信目前只有文本消息支持@
                        if (CollectionUtils.isNotEmpty(receivers)) {
                            for (Receiver receiver : receivers) {
                                if (StringUtils.isBlank(receiver.getPhone())) {
                                    continue;
                                }
                                // 处理消息发送配置发送webhook消息传递手机号中使用逗号拼接多个手机号的情况
                                phoneSet.addAll(Arrays.asList(StringUtils.split(receiver.getPhone(), BaseConstants.Symbol.COMMA)));
                            }
                        }
                        sendWhWeChatTextMessage(messageSender, message, phoneSet, tryTimes);
                        break;
                    case MARK_DOWN:
                    default:
                        sendWhWeChatMarkdownMessage(messageSender, message, tryTimes);
                        break;
                }
                break;
            default:
                throw new CommonException(HmsgConstant.ErrorCode.WEBHOOK_TYPE_ILLEGAL);
        }
    }

    /**
     * 发送企业微信 text WebHook消息
     *
     * @param messageSender WebHookSender
     * @param message       消息内容类
     */
    private void sendWhWeChatTextMessage(WebHookSender messageSender, Message message, Set<String> phones, Integer tryTimes) throws ExecutionException, RetryException {
        String msgContent = StringUtils.isEmpty(message.getPlainContent()) ? message.getContent() : message.getPlainContent();
        String content = msgContent
                .replaceAll("<(?!font|/font).*?>", "")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&amp;", "&");
        content = this.urlEncode(content);
        WhWeChatText whWeChatText = new WhWeChatText();
        WhWeChatText.Content textContent = new WhWeChatText.Content();
        textContent.setContent(content);
        // 企业微信只有text类型消息支持@
        textContent.setMentioned_mobile_list(phones);
        whWeChatText.setText(textContent);
        RestTemplate restTemplate = new RestTemplate();

        Retryer<?> retry = messageSendRetryer.buildRetry(tryTimes);
        retry.call(() -> {
            restTemplate.postForEntity(messageSender.getWebhookAddress(), whWeChatText, String.class);
            return null;
        });
    }

    /**
     * 发送企业微信 markdown WebHook消息
     *
     * @param messageSender WebHookSender
     * @param message       消息内容类
     */
    private void sendWhWeChatMarkdownMessage(WebHookSender messageSender, Message message, Integer tryTimes) throws ExecutionException, RetryException {
        String msgContent = StringUtils.isEmpty(message.getPlainContent()) ? message.getContent() : message.getPlainContent();
        String content = msgContent
                .replaceAll("<(?!font|/font).*?>", "")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&amp;", "&");
        content = this.urlEncode(content);
        WhWeChatMarkdown whWeChatMarkdown = new WhWeChatMarkdown();
        WhWeChatMarkdown.Content markdownContent = new WhWeChatMarkdown.Content();
        markdownContent.setContent(content);
        whWeChatMarkdown.setMarkdown(markdownContent);
        RestTemplate restTemplate = new RestTemplate();

        Retryer<?> retry = messageSendRetryer.buildRetry(tryTimes);
        retry.call(() -> {
            restTemplate.postForEntity(messageSender.getWebhookAddress(), whWeChatMarkdown, String.class);
            return null;
        });
    }

    /**
     * 发送钉钉 text WebHook消息
     *
     * @param messageSender WebHookSender
     * @param message       消息内容类
     * @param phones        需要@的手机号
     */
    @SuppressWarnings("DuplicatedCode")
    private void sendWhDingTalkTextMessage(WebHookSender messageSender, Message message, Set<String> phones, Integer tryTimes) throws ExecutionException, RetryException {
        String msgContent = StringUtils.isEmpty(message.getPlainContent()) ? message.getContent() : message.getPlainContent();
        WhDingTalkText whDingTalkText = new WhDingTalkText();
        WhDingTalkText.WhDingAt whDingAt = new WhDingTalkText.WhDingAt();
        WhDingTalkText.Content textContent = new WhDingTalkText.Content();
        // 转换内容格式
        String contentText = String.format("%s", msgContent
                .replaceAll("<[^>]+>", "")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&amp;", "&"));
        if (CollectionUtils.isNotEmpty(phones)) {
            for (String receiver : phones) {
                // 消息内容text内要带上"@手机号"，跟atMobiles参数结合使用，才有@效果
                contentText = StringUtils.join(contentText, BaseConstants.Symbol.SPACE, BaseConstants.Symbol.AT, receiver);
            }
            whDingAt.setAtAll(false);
            whDingAt.setAtMobiles(phones);
        } else {
            whDingAt.setAtAll(true);
        }
        // 1.添加@对象
        whDingTalkText.setAt(whDingAt);
        // 2.添加安全设置，构造请求uri（此处直接封装uri而非用String类型来进行http请求：RestTemplate
        // 在执行请求时，如果路径为String类型，将分析路径参数并组合路径，此时会丢失sign的部分特殊字符）
        long timestamp = SystemClock.now();
        URI uri;
        try {
            uri = new URI(messageSender.getWebhookAddress() + "&timestamp=" + timestamp + "&sign="
                    + addSignature(messageSender.getSecret(), timestamp));
        } catch (URISyntaxException e) {
            throw new CommonException(e);
        }
        // 3.添加发送内容
        textContent.setText(contentText);
        textContent.setTitle(message.getSubject());
        whDingTalkText.setText(textContent);
        // 4.发送请求
        RestTemplate restTemplate = new RestTemplate();

        Retryer<?> retry = messageSendRetryer.buildRetry(tryTimes);
        retry.call(() -> {
            restTemplate.postForEntity(uri, whDingTalkText, String.class);
            return null;
        });
    }

    /**
     * 发送钉钉 markdown WebHook消息
     *
     * @param messageSender WebHookSender
     * @param message       消息内容类
     * @param phones        需要@的手机号
     */
    @SuppressWarnings("DuplicatedCode")
    private void sendWhDingTalkMarkdownMessage(WebHookSender messageSender, Message message, Set<String> phones, Integer tryTimes) throws ExecutionException, RetryException {
        String msgContent = StringUtils.isEmpty(message.getPlainContent()) ? message.getContent() : message.getPlainContent();
        WhDingTalkMarkdown whDingTalkMarkdown = new WhDingTalkMarkdown();
        WhDingTalkMarkdown.WhDingAt whDingAt = new WhDingTalkMarkdown.WhDingAt();
        WhDingTalkMarkdown.Content markdownContent = new WhDingTalkMarkdown.Content();
        // 转换内容格式
        String contentText = String.format("%s", msgContent
                .replaceAll("<[^>]+>", "")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&amp;", "&"));
        if (CollectionUtils.isNotEmpty(phones)) {
            for (String receiver : phones) {
                contentText = StringUtils.join(contentText, BaseConstants.Symbol.SPACE, BaseConstants.Symbol.AT,
                        receiver);
            }
            whDingAt.setAtAll(false);
            whDingAt.setAtMobiles(phones);
        } else {
            whDingAt.setAtAll(true);
        }
        // 1.添加@对象
        whDingTalkMarkdown.setAt(whDingAt);
        // 2.添加安全设置，构造请求uri（此处直接封装uri而非用String类型来进行http请求：RestTemplate
        // 在执行请求时，如果路径为String类型，将分析路径参数并组合路径，此时会丢失sign的部分特殊字符）
        long timestamp = SystemClock.now();
        URI uri;
        try {
            uri = new URI(messageSender.getWebhookAddress() + "&timestamp=" + timestamp + "&sign="
                    + addSignature(messageSender.getSecret(), timestamp));
        } catch (URISyntaxException e) {
            throw new CommonException(e);
        }
        // 3.添加发送内容
        markdownContent.setText(contentText);
        markdownContent.setTitle(message.getSubject());
        whDingTalkMarkdown.setMarkdown(markdownContent);
        // 4.发送请求
        RestTemplate restTemplate = new RestTemplate();

        Retryer<?> retry = messageSendRetryer.buildRetry(tryTimes);
        retry.call(() -> {
            restTemplate.postForEntity(uri, whDingTalkMarkdown, String.class);
            return null;
        });
    }

    /**
     * 发送Json消息
     * Json 类型的请求头认证信息需传递的格式为Authorization:bearer 8ebe3da9-0337-4e0a-866a-671f8fa90f9c
     *
     * @param messageSender WebHookSender
     */
    private void sendWHJsonMessage(WebHookSender messageSender, Message message, Integer tryTimes) throws ExecutionException, RetryException {
        String msgContent = StringUtils.isEmpty(message.getPlainContent()) ? message.getContent() : message.getPlainContent();
        HttpHeaders httpHeaders = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        httpHeaders.setContentType(type);
        if (messageSender.getSecret() != null) {
            String[] split = StringUtils.split(messageSender.getSecret(), ":", 2);
            if (split.length == BaseConstants.Digital.TWO) {
                httpHeaders.add(split[0], split[1]);
            }
        }
        HttpEntity<String> request = new HttpEntity<>(msgContent, httpHeaders);
        RestTemplate restTemplate = new RestTemplate();

        Retryer<?> retry = messageSendRetryer.buildRetry(tryTimes);
        retry.call(() -> {
            restTemplate.postForEntity(messageSender.getWebhookAddress(), request, String.class);
            return null;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message resendWebHookMessage(UserMessageDTO message) {
        // 重新生成消息内容
        Message messageContent = messageGeneratorService.generateMessage(message.getTenantId(), message.getTemplateCode(), message.getLang(), message.getArgs());
        if (messageContent != null) {
            message.setPlainContent(messageContent.getPlainContent());
        }
        WebhookServerConfig dbWebHook = WebhookServerConfig.getConfig(message.getTenantId(), message.getServerCode());
        Assert.notNull(dbWebHook, BaseConstants.ErrorCode.NOT_NULL);
        if (StringUtils.isNotBlank(dbWebHook.getSecret())) {
            dbWebHook.setSecret(this.decryptSecret(dbWebHook.getSecret()));
        }
        List<Receiver> receivers = new ArrayList<>();
        try {
            if (CollectionUtils.isNotEmpty(message.getMessageReceiverList())) {
                message.getMessageReceiverList().forEach(receiver -> {
                    if (!org.springframework.util.StringUtils.hasText(receiver.getReceiverAddress())
                            || receiver.getTenantId() == null) {
                        throw new CommonException("Sending a message error because no recipient or target tenant is specified : " + receiver);
                    }
                    Receiver rc = new Receiver();
                    rc.setIdd(receiver.getIdd());
                    rc.setPhone(receiver.getReceiverAddress());
                    receivers.add(rc);
                });
            }
            // 发送消息
            WebHookSender webHookSender = new WebHookSender()
                    .setMessage(CommonConverter.beanConvert(org.hzero.boot.message.entity.Message.class,
                            message.getMessage()))
                    .setServerCode(message.getServerCode())
                    .setReceiverAddressList(message.getReceiverAddressList());
            webHookSender.setMessageCode(message.getMessageCode());
            webHookSender.setTenantId(message.getTenantId());
            webHookSender.setLang(message.getLang());
            webHookSender.setServerType(dbWebHook.getServerType());
            webHookSender.setWebhookAddress(dbWebHook.getWebhookAddress());
            webHookSender.setSecret(dbWebHook.getSecret());
            webHookSender.setReceiverAddressList(receivers);
            // 重新发送消息
            sendWHMessage(webHookSender, message, null);
            successProcessUpdate(message);
        } catch (Exception e) {
            failedProcessUpdate(message, e);
        }
        return message;
    }

    /**
     * 钉钉加签方法 第一步，把timestamp+"\n"+密钥当做签名字符串，使用HmacSHA256算法计算签名，然后进行Base64
     * encode，最后再把签名参数再进行urlEncode，得到最终的签名（需要使用UTF-8字符集）
     *
     * @param secret    秘钥
     * @param timestamp 时间戳
     * @return 加签字符串
     */
    private String addSignature(String secret, Long timestamp) {
        // 第一步，把timestamp+"\n"+密钥当做签名字符串
        String stringToSign = timestamp + "\n" + secret;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            return URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
        } catch (Exception e) {
            LOGGER.error(">>>SENDING_WEBHOOK_ERROR>>> An error occurred while adding the signature {}", e.getMessage());
            return null;
        }
    }

    /**
     * 解密Secret
     *
     * @return 解密后的Secret
     */
    private String decryptSecret(String secret) {
        try {
            // 设置解密后的秘钥
            return DataSecurityHelper.decrypt(secret);
        } catch (Exception e) {
            LOGGER.warn("===>>secret decryption failed, use the original secret to perform message sending<<===");
        }
        return secret;
    }

    /**
     * 转义URL中的中文字符
     */
    private String urlEncode(String content) {
        Set<String> urlSet = patternMessageContent(content, URL_REGEX);
        for (String url : urlSet) {
            String encodeUrl = encodeUrlChinese(url);
            content = content.replace(url, encodeUrl);
        }
        return content;
    }

    /**
     * 处理URL中的中文参数
     *
     * @param url url路径
     */
    private String encodeUrlChinese(String url) {
        Set<String> chineseSet = patternMessageContent(url, CHINESE_REGEX);
        for (String chinese : chineseSet) {
            try {
                String encodeChinese = URLEncoder.encode(chinese, StandardCharsets.UTF_8.name());
                url = StringUtils.replace(url, chinese, encodeChinese);
            } catch (Exception e) {
                LOGGER.error("url encode failed, exception is : {}", e.getMessage());
                throw new CommonException(BaseConstants.ErrorCode.ERROR, e);
            }
        }
        return url;
    }

    /**
     * 正则匹配消息内容
     *
     * @param content 需匹配的内容
     * @param regex   正则表达式
     */
    private Set<String> patternMessageContent(String content, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        Set<String> resultSet = new LinkedHashSet<>();
        while (matcher.find()) {
            resultSet.add(matcher.group());
        }
        return resultSet;
    }

}
