package io.choerodon.message.app.service.impl;

import static io.choerodon.message.infra.constant.Constants.*;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.message.entity.DingTalkSender;
import org.hzero.boot.message.entity.MessageSender;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.boot.message.entity.WebHookSender;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.safe.SafeRedisHelper;
import org.hzero.message.app.service.DingTalkSendService;
import org.hzero.message.app.service.MessageReceiverService;
import org.hzero.message.app.service.TemplateServerService;
import org.hzero.message.app.service.impl.RelSendMessageServiceImpl;
import org.hzero.message.domain.entity.Message;
import org.hzero.message.domain.entity.TemplateServer;
import org.hzero.message.domain.entity.TemplateServerLine;
import org.hzero.message.domain.entity.WebhookServer;
import org.hzero.message.domain.repository.TemplateServerLineRepository;
import org.hzero.message.infra.constant.HmsgConstant;
import org.hzero.message.infra.mapper.WebhookServerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import io.choerodon.asgard.common.ApplicationContextHelper;
import io.choerodon.core.enums.MessageAdditionalType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.utils.TypeUtils;
import io.choerodon.message.api.vo.UserVO;
import io.choerodon.message.app.service.EmailTemplateConfigService;
import io.choerodon.message.app.service.RelSendMessageC7nService;
import io.choerodon.message.infra.constant.Constants;
import io.choerodon.message.infra.dto.EmailTemplateConfigDTO;
import io.choerodon.message.infra.dto.MessageSettingDTO;
import io.choerodon.message.infra.dto.WebhookProjectRelDTO;
import io.choerodon.message.infra.dto.iam.ProjectDTO;
import io.choerodon.message.infra.dto.iam.TenantDTO;
import io.choerodon.message.infra.feign.IamFeignClient;
import io.choerodon.message.infra.mapper.MessageSettingC7nMapper;
import io.choerodon.message.infra.mapper.ReceiveSettingC7nMapper;
import io.choerodon.message.infra.mapper.WebhookProjectRelMapper;
import io.choerodon.message.infra.utils.JsonHelper;

/**
 * @author scp
 * @date 2020/5/13
 * @description
 */
@Service
public class RelSendMessageC7nServiceImpl extends RelSendMessageServiceImpl implements RelSendMessageC7nService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String NO_SEND_WEBHOOK = "NoSendWebHook";
    private static final String NO_SEND_WEB = "NoSendWeb";
    private static final String NO_SEND_EMAIL = "NoSendEmail";
    private static final String NO_SEND_SMS = "NoSendSms";
    private static final String OBJECT_KIND = "objectKind";
    private static final String CREATED_AT = "createdAt";
    private static final String EVENT_NAME = "eventName";


    private static final String SOURCE_LEVEL = "sourceLevel";

    @Autowired
    private TemplateServerService templateServerService;
    @Autowired
    private ReceiveSettingC7nMapper receiveSettingC7nMapper;
    @Autowired
    private MessageSettingC7nMapper messageSettingC7nMapper;
    @Autowired
    private WebhookProjectRelMapper webhookProjectRelMapper;
    @Autowired
    private WebhookServerMapper webhookServerMapper;
    @Autowired
    private MessageReceiverService messageReceiverService;
    @Autowired
    private TemplateServerLineRepository templateServerLineRepository;
    @Autowired
    private DingTalkSendService dingTalkSendService;
    @Autowired
    private IamFeignClient iamFeignClient;
    @Autowired
    private EmailTemplateConfigService emailTemplateConfigService;


    @Override
    public List<Message> relSendMessageReceipt(MessageSender messageSender, Long organizationId) {
        TemplateServer templateServer = templateServerService.getTemplateServer(messageSender.getTenantId(), messageSender.getMessageCode());
        if (templateServer == null) {
            throw new CommonException("message.code.not.exit:" + messageSender.getMessageCode());
        }
        Long tenantId = getTenantId(messageSender);
        setEmailConfigArgs(messageSender, tenantId);
        return super.relSendMessageReceipt(messageSender, tenantId);
    }

    @Override
    protected void filterWebReceiver(MessageSender sender) {
        //如果有特殊标志则不发送
        if (isSend(sender, NO_SEND_WEB)) return;
        super.filterWebReceiver(sender);
        filterReceiver(sender, HmsgConstant.MessageType.WEB);
    }

    private boolean isSend(MessageSender sender, String noSendWeb) {
        if (!CollectionUtils.isEmpty(sender.getAdditionalInformation()) && !ObjectUtils.isEmpty(sender.getAdditionalInformation().get(noSendWeb))) {
            sender.setReceiverAddressList(Collections.EMPTY_LIST);
            return true;
        }
        return false;
    }

    @Override
    protected void filterSmsReceiver(MessageSender sender) {
        if (isSend(sender, NO_SEND_SMS)) return;
        super.filterSmsReceiver(sender);
        filterReceiver(sender, HmsgConstant.MessageType.SMS);
    }

    @Override
    protected void filterEmailReceiver(MessageSender sender) {
        if (isSend(sender, NO_SEND_EMAIL)) return;
        super.filterEmailReceiver(sender);
        filterReceiver(sender, HmsgConstant.MessageType.EMAIL);
    }

    @Override
    protected void filterWebHookReceiver(MessageSender sender, List<WebHookSender> webHookSenderList) {
        if (!CollectionUtils.isEmpty(sender.getAdditionalInformation()) &&
                !ObjectUtils.isEmpty(sender.getAdditionalInformation().get(NO_SEND_WEBHOOK))) {
            webHookSenderList.clear();
        }
        super.filterWebHookReceiver(sender, webHookSenderList);
        webHookFilter(sender, webHookSenderList);
    }

    private void filterReceiver(MessageSender messageSender, String messageType) {
//        logger.info(">>>>>>>>>>>filterReceiver messageSender start :{}>>>>>>>>>>>>>>>>>>>>", JsonHelper.marshalByJackson(messageSender));
        TemplateServer templateServer = templateServerService.getTemplateServer(messageSender.getTenantId(), messageSender.getMessageCode());
        Long tempServerId = templateServer.getTempServerId();
        Long projectId = null;
        Long tenantId = null;
        String sourceLevel = null;
        Long envId = null;
        String eventName = null;
        if (!CollectionUtils.isEmpty(messageSender.getAdditionalInformation()) &&
                !ObjectUtils.isEmpty(messageSender.getAdditionalInformation().get(MessageAdditionalType.PARAM_PROJECT_ID.getTypeName()))) {
            projectId = Long.valueOf(String.valueOf(messageSender.getAdditionalInformation().get(MessageAdditionalType.PARAM_PROJECT_ID.getTypeName())));
        }
        if (!CollectionUtils.isEmpty(messageSender.getAdditionalInformation()) &&
                !ObjectUtils.isEmpty(messageSender.getAdditionalInformation().get(MessageAdditionalType.PARAM_TENANT_ID.getTypeName()))) {
            tenantId = Long.valueOf(String.valueOf(messageSender.getAdditionalInformation().get(MessageAdditionalType.PARAM_TENANT_ID.getTypeName())));
        }
        if (!CollectionUtils.isEmpty(messageSender.getAdditionalInformation()) &&
                !ObjectUtils.isEmpty(messageSender.getAdditionalInformation().get(SOURCE_LEVEL))) {
            sourceLevel = String.valueOf(messageSender.getAdditionalInformation().get(SOURCE_LEVEL));
        }

        if (!CollectionUtils.isEmpty(messageSender.getAdditionalInformation()) &&
                !ObjectUtils.isEmpty(messageSender.getAdditionalInformation().get(MessageAdditionalType.PARAM_ENV_ID.getTypeName()))) {
            envId = Long.valueOf(String.valueOf(messageSender.getAdditionalInformation().get(MessageAdditionalType.PARAM_ENV_ID.getTypeName())));
        }
        if (!CollectionUtils.isEmpty(messageSender.getAdditionalInformation()) &&
                !ObjectUtils.isEmpty(messageSender.getAdditionalInformation().get(MessageAdditionalType.PARAM_EVENT_NAME.getTypeName()))) {
            eventName = String.valueOf(messageSender.getAdditionalInformation().get(MessageAdditionalType.PARAM_EVENT_NAME.getTypeName()));
        }
        List<Receiver> receiverList = messageSender.getReceiverAddressList();
        if (messageType.equals(HmsgConstant.MessageType.WEB) || messageType.equals(HmsgConstant.MessageType.EMAIL) || messageType.equals(HmsgConstant.MessageType.DT)) {
            receiveFilter(receiverList, tempServerId, projectId, messageType);
        }
        // 如果项目层未启动 则不发送 接受者为null
        if ((messageType.equals(HmsgConstant.MessageType.WEB) ||
                messageType.equals(HmsgConstant.MessageType.EMAIL) ||
                messageType.equals(HmsgConstant.MessageType.SMS) ||
                messageType.equals(HmsgConstant.MessageType.DT))
                && messageSettingC7nMapper.selectProjectMessage(ResourceLevel.PROJECT.value()).contains(messageSender.getMessageCode())) {
            //项目层设置未开启
            if (!projectFilter(messageSender, projectId, envId, eventName, messageType)) {
                receiverList.clear();
            }
        }
        //组织层拦截配置
        if (StringUtils.equalsIgnoreCase(sourceLevel, ResourceLevel.ORGANIZATION.value()) && tenantId != null) {
            if ((messageType.equals(HmsgConstant.MessageType.WEB) ||
                    messageType.equals(HmsgConstant.MessageType.EMAIL) ||
                    messageType.equals(HmsgConstant.MessageType.DT))
                    && messageSettingC7nMapper.selectProjectMessage(ResourceLevel.ORGANIZATION.value()).contains(messageSender.getMessageCode())) {
                //项目层设置未开启
                if (!orgFilter(messageSender, tenantId, messageType)) {
                    receiverList.clear();
                }
            }
        }
        logger.info(">>>>>>>>>>>filterReceiver messageSender end :{}>>>>>>>>>>>>>>>>>>>>", JsonHelper.marshalByJackson(messageSender));
    }

    private boolean orgFilter(MessageSender messageSender, Long organizationId, String messageType) {
        //1.查询项目下是否设置了改消息的发送设置.没有就用默认的
        MessageSettingDTO messageSettingDTO = messageSettingC7nMapper.selectByParams(organizationId, ResourceLevel.ORGANIZATION.value(), messageSender.getMessageCode(), null, null, messageType);
        //如果项目下配置没有开启，则查询默认配置
        if (Objects.isNull(messageSettingDTO)) {
            messageSettingDTO = messageSettingC7nMapper.selectByParams(BaseConstants.DEFAULT_TENANT_ID, ResourceLevel.ORGANIZATION.value(), messageSender.getMessageCode(), null, null, messageType);
        }
        //根据消息配置返回项目层是否应该发送消息
        if (ObjectUtils.isEmpty(messageSettingDTO)) {
            return Boolean.FALSE;
        }
        if (HmsgConstant.MessageType.WEB.equals(messageType)) {
            return messageSettingDTO.getPmEnable();
        }
        if (HmsgConstant.MessageType.EMAIL.equals(messageType)) {
            return messageSettingDTO.getEmailEnable();
        }
        if (HmsgConstant.MessageType.SMS.equals(messageType)) {
            return messageSettingDTO.getSmsEnable();
        }
        if (HmsgConstant.MessageType.DT.equals(messageType)) {
            return messageSettingDTO.getDtEnable();
        } else {
            return Boolean.FALSE;
        }
    }


    /**
     * 个人接收设置 消息过滤
     *
     * @param receiverAddressList
     * @param tempServerId
     * @param projectId
     */
    private void receiveFilter(List<Receiver> receiverAddressList, Long tempServerId, Long projectId, String messageType) {
        if (!CollectionUtils.isEmpty(receiverAddressList)) {
            List<Long> userIds = receiverAddressList.stream().map(Receiver::getUserId).collect(Collectors.toList());
            List<Long> removeUserIds = receiveSettingC7nMapper.selectByTemplateServerId(projectId, tempServerId, userIds, messageType);
            List<Receiver> removeUserList = receiverAddressList.stream().filter(t -> removeUserIds.contains(t.getUserId())).collect(Collectors.toList());
            receiverAddressList.removeAll(removeUserList);
        }
    }

    /**
     * 项目层 对应消息是否启用
     *
     * @param messageSender
     * @param projectId
     * @param envId
     * @param eventName
     * @param messageType
     * @return
     */
    private Boolean projectFilter(MessageSender messageSender, Long projectId, Long envId, String eventName, String messageType) {
        //1.查询项目下是否设置了改消息的发送设置.没有就用默认的
        MessageSettingDTO messageSettingDTO = messageSettingC7nMapper.selectByParams(projectId, ResourceLevel.PROJECT.value(), messageSender.getMessageCode(), envId, eventName, messageType);
        //如果项目下配置没有开启，则查询默认配置
        if (Objects.isNull(messageSettingDTO)) {
            messageSettingDTO = messageSettingC7nMapper.selectByParams(0L, ResourceLevel.PROJECT.value(), messageSender.getMessageCode(), null, eventName, messageType);
        }
        //根据消息配置返回项目层是否应该发送消息
        if (ObjectUtils.isEmpty(messageSettingDTO)) {
            return Boolean.FALSE;
        }
        if (HmsgConstant.MessageType.WEB.equals(messageType)) {
            return messageSettingDTO.getPmEnable();
        }
        if (HmsgConstant.MessageType.EMAIL.equals(messageType)) {
            return messageSettingDTO.getEmailEnable();
        }
        if (HmsgConstant.MessageType.SMS.equals(messageType)) {
            return messageSettingDTO.getSmsEnable();
        }
        if (HmsgConstant.MessageType.DT.equals(messageType)) {
            return messageSettingDTO.getDtEnable();
        } else {
            return Boolean.FALSE;
        }
    }


    /**
     * webhook过滤
     *
     * @param messageSender
     * @param webHookSenderList
     */
    private void webHookFilter(MessageSender messageSender, List<WebHookSender> webHookSenderList) {
        Long projectId = null;
        if (!CollectionUtils.isEmpty(messageSender.getAdditionalInformation()) &&
                !ObjectUtils.isEmpty(messageSender.getAdditionalInformation().get(MessageAdditionalType.PARAM_PROJECT_ID.getTypeName()))) {
            projectId = Long.valueOf(String.valueOf(messageSender.getAdditionalInformation().get(MessageAdditionalType.PARAM_PROJECT_ID.getTypeName())));
        }
        Long tenantId = null;
        if (!CollectionUtils.isEmpty(messageSender.getAdditionalInformation()) &&
                !ObjectUtils.isEmpty(messageSender.getAdditionalInformation().get(MessageAdditionalType.PARAM_TENANT_ID.getTypeName()))) {
            tenantId = Long.valueOf(String.valueOf(messageSender.getAdditionalInformation().get(MessageAdditionalType.PARAM_TENANT_ID.getTypeName())));
        }
        // 组织id和项目id都未传 不能发送
        if (tenantId == null && projectId == null) {
            webHookSenderList.clear();
        }
        List<String> webServerCodes;
        List<WebHookSender> senderList;
        if (!ObjectUtils.isEmpty(projectId)) {
            //项目成获取到应该发送的webhook地址
            webServerCodes = webhookProjectRelMapper.selectByProjectId(projectId).stream().map(WebhookProjectRelDTO::getServerCode).collect(Collectors.toList());
            //获取本项目的webhook发送地址
            senderList = webHookSenderList.stream().filter(t -> webServerCodes.contains(t.getServerCode())).collect(Collectors.toList());
        } else {
            //获取本组织下要发送webhook地址
            WebhookServer webHookSender = new WebhookServer();
            webHookSender.setTenantId(tenantId);
            webHookSender.setEnabledFlag(1);
            webServerCodes = webhookServerMapper.select(webHookSender).stream().map(WebhookServer::getServerCode).collect(Collectors.toList());
            //获取本组织的webhook发送地址
            senderList = webHookSenderList.stream().filter(t -> webServerCodes.contains(t.getServerCode())
                    && t.getTenantId().equals(Long.valueOf(String.valueOf(messageSender.getAdditionalInformation().get(MessageAdditionalType.PARAM_TENANT_ID.getTypeName()))))).collect(Collectors.toList());

        }
        webHookSenderList.clear();
        senderList.forEach(webHookSender -> {
            webHookSender.setMessage(null);
        });
        webHookSenderList.addAll(senderList);
        //如果是钉钉类型的消息清除接收者
        if (!CollectionUtils.isEmpty(webHookSenderList)) {
            //为webhook Json 类型加上固有的字段参数
            Map<String, Object> senderObjectArgs = messageSender.getObjectArgs();
            Map<String, String> messageSenderArgs = messageSender.getArgs();
            Map<String, String> reSenderArgs = new HashMap<>();
            reSenderArgs = getSenderArgs(senderObjectArgs, messageSenderArgs, messageSender.getMessageCode());
            for (WebHookSender webHookSender : webHookSenderList) {
                webHookSender.setLang("zh_CN");
                webHookSender.setReceiverAddressList(null);
                if (!org.apache.commons.collections4.MapUtils.isEmpty(reSenderArgs)) {
                    //重新设置参数填充
                    webHookSender.setArgs(reSenderArgs);
                }
            }
        }
        logger.info(">>>>>>>>>>>messageSender2:{}>>>>>>>>>>>>>>>>>>>>", JsonHelper.marshalByJackson(messageSender));
        messageSender.setReceiverAddressList(null);
    }

    private Map<String, String> getSenderArgs(Map<String, Object> senderObjectArgs, Map<String, String> messageSenderArgs, String messageCode) {
        Map<String, String> reSenderArgs = new HashMap<>();
        if (MapUtils.isEmpty(senderObjectArgs) && MapUtils.isEmpty(messageSenderArgs)) {
            return reSenderArgs;
        }
        if (!MapUtils.isEmpty(senderObjectArgs)) {
            for (Map.Entry<String, Object> stringObjectEntry : senderObjectArgs.entrySet()) {
                reSenderArgs.put(stringObjectEntry.getKey(), stringObjectEntry.getValue().toString());
            }
            TemplateServer templateServer = templateServerService.getTemplateServer(BaseConstants.DEFAULT_TENANT_ID, messageCode);
            reSenderArgs.put(OBJECT_KIND, templateServer.getMessageCode());
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
            reSenderArgs.put(CREATED_AT, dateFormat.format(date));
            reSenderArgs.put(EVENT_NAME, templateServer.getMessageName());
            return reSenderArgs;
        }
        if (!MapUtils.isEmpty(messageSenderArgs)) {
            for (Map.Entry<String, String> stringObjectEntry : messageSenderArgs.entrySet()) {
                reSenderArgs.put(stringObjectEntry.getKey(), stringObjectEntry.getValue().toString());
            }
            TemplateServer templateServer = templateServerService.getTemplateServer(BaseConstants.DEFAULT_TENANT_ID, messageCode);
            reSenderArgs.put(OBJECT_KIND, templateServer.getMessageCode());
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
            reSenderArgs.put(CREATED_AT, dateFormat.format(date));
            reSenderArgs.put(EVENT_NAME, templateServer.getMessageName());
            return reSenderArgs;
        }
        return reSenderArgs;

    }

    @Override
    @Async
    public void batchSendMessage(List<MessageSender> senderList) {
        if (CollectionUtils.isEmpty(senderList)) {
            return;
        }
        senderList.forEach(sender -> {
            try {
                this.c7nRelSendMessageReceipt(sender, sender.getTenantId());
            } catch (Exception e) {
                logger.error("batch send message error", e);
            }
        });
    }

    @Override
    public List<Message> c7nRelSendMessageReceipt(MessageSender messageSender, Long organizationId) {
        if (organizationId != null) {
            messageSender.setTenantId(organizationId);
        }

        this.validObject(messageSender);
        if (messageSender.getReceiverAddressList() == null) {
            messageSender.setReceiverAddressList(Collections.emptyList());
        }
        // 设置邮件模板参数
        Long tenantId = getTenantId(messageSender);
        setEmailConfigArgs(messageSender, tenantId);

        messageSender = this.messageReceiverService.queryReceiver(messageSender);
        TemplateServer templateServer = this.templateServerService.getTemplateServer(messageSender.getTenantId(), messageSender.getMessageCode());
        Assert.notNull(templateServer, "error.data_not_exists");
        Assert.isTrue(Objects.equals(templateServer.getEnabledFlag(), BaseConstants.Flag.YES), "error.data_not_exists");
        if (StringUtils.isBlank(messageSender.getReceiveConfigCode())) {
            messageSender.setReceiveConfigCode(templateServer.getMessageCode());
        }
        Map<String, List<TemplateServerLine>> serverLineMap = this.templateServerLineRepository.enabledTemplateServerLine(templateServer.getTempServerId(), templateServer.getTenantId()).stream().collect(Collectors.groupingBy(TemplateServerLine::getTypeCode));
        List<Message> results = new ArrayList<>();
        List<Message> dingTalkResults = new ArrayList<>();
        try {
            if (serverLineMap.containsKey("DT") && this.c7nSendEnable(messageSender.getTypeCodeList(), "DT")) {
                this.sendDingTalk(serverLineMap, dingTalkResults, new MessageSender(messageSender), templateServer.getCategoryCode());
                results.addAll(dingTalkResults);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        serverLineMap.remove("DT");
        Class<?> clazz = this.getClass().getSuperclass();
        while (!clazz.isAssignableFrom(RelSendMessageServiceImpl.class)) {
            clazz = clazz.getSuperclass();
        }
        try {
            Method sendMessageMethod = clazz.getDeclaredMethod("sendMessage", Map.class, MessageSender.class);
            sendMessageMethod.setAccessible(true);
            results.addAll((List) sendMessageMethod.invoke(this, serverLineMap, messageSender));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    @Override
    public void sendDingTalk(Map<String, List<TemplateServerLine>> serverLineMap, List<Message> result, MessageSender sender, String categoryCode) {
        List<TemplateServerLine> templateServerLineList = serverLineMap.get("DT");
        this.filterDingTalkReceiver(sender);
        List<Long> userIdList = sender.getReceiverAddressList().stream().map(Receiver::getUserId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        // 表示发送平台层的消息，那么此时需要找出每个用户对应的组织，按组织来发送
        if ("SITE".equals(categoryCode)) {
            List<UserVO> userVOS = iamFeignClient.queryUserOrgId(userIdList).getBody();
            Map<Long, List<UserVO>> userVOMapGroupingByOrgId = userVOS.stream().collect(Collectors.groupingBy(UserVO::getOrganizationId));
            for (Map.Entry<Long, List<UserVO>> entry : userVOMapGroupingByOrgId.entrySet()) {
                Long orgId = entry.getKey();
                List<UserVO> users = entry.getValue();
                Map<Long, String> openUserIdMap = iamFeignClient.getOpenUserIdsByUserIds(userIdList, orgId, DING_TALK_OPEN_APP_CODE).getBody();
                if (ObjectUtils.isEmpty(openUserIdMap)) {
                    continue;
                }
                List<Long> userIds = users.stream().map(UserVO::getId).collect(Collectors.toList());
                List<String> openUserIdList = new ArrayList<>();
                openUserIdMap.forEach((userId, openId) -> {
                    if (userIds.contains(userId)) {
                        openUserIdList.add(openId);
                    }
                });
                sendDingTalkMessage(orgId, openUserIdList, templateServerLineList, sender, result);
            }
        } else {
            Long tenantId = null;
            Optional<Object> projectIdOptional = Optional.ofNullable(sender.getAdditionalInformation())
                    .map(additionalInformation -> additionalInformation.get(MessageAdditionalType.PARAM_PROJECT_ID.getTypeName()));
            Optional<Object> tenantOptional = Optional.ofNullable(sender.getAdditionalInformation())
                    .map(additionalInformation -> additionalInformation.get(MessageAdditionalType.PARAM_TENANT_ID.getTypeName()));
            if (!tenantOptional.isPresent() && projectIdOptional.isPresent()) {
                Long projectId = TypeUtils.objToLong(projectIdOptional.get());
                ProjectDTO projectDTO = iamFeignClient.queryProjectByIdWithoutExtraInfo(projectId, false, false, false).getBody();
                if (!ObjectUtils.isEmpty(projectDTO)) {
                    tenantId = projectDTO.getOrganizationId();
                }
            } else {
                tenantId = Optional.ofNullable(sender.getAdditionalInformation())
                        .map(additionalInformation -> additionalInformation.get(MessageAdditionalType.PARAM_TENANT_ID.getTypeName()))
                        .map(TypeUtils::objToLong)
                        // 兼容班翎工作流发消息的请求
                        // gaokuo.dai@zknow.com 2022-08-11
                        .orElse(sender.getTenantId());
            }
            Map<Long, String> openUserIdMap = iamFeignClient.getOpenUserIdsByUserIds(userIdList, tenantId, DING_TALK_OPEN_APP_CODE).getBody();
            if (!ObjectUtils.isEmpty(openUserIdMap)) {
                List<String> openUserIdList = new ArrayList<>(openUserIdMap.values());
                sendDingTalkMessage(tenantId, openUserIdList, templateServerLineList, sender, result);
            }
        }
    }

    private void sendDingTalkMessage(Long tenantId, List<String> openUserIdList, List<TemplateServerLine> templateServerLineList, MessageSender sender, List<Message> result) {
        DingTalkSender dingTalkSender = (new DingTalkSender()).setTenantId(tenantId).setReceiveConfigCode(sender.getReceiveConfigCode()).setLang(sender.getLang()).setUserIdList(openUserIdList).setSourceKey(sender.getSourceKey());
        Map<String, String> args = new HashMap<>();
        if (!CollectionUtils.isEmpty(sender.getArgs())) {
            args.putAll(sender.getArgs());
        }
        if (!CollectionUtils.isEmpty(sender.getObjectArgs())) {
            sender.getObjectArgs().forEach((key, value) -> args.put(key, (String) value));
        }
        dingTalkSender.setServerCode(DING_TALK_SERVER_CODE);
        dingTalkSender.setArgs(args);
        if (!CollectionUtils.isEmpty(openUserIdList)) {
            for (TemplateServerLine line : templateServerLineList) {
                Message msg = this.dingTalkSendService.sendMessage(dingTalkSender.setMessageCode(line.getTemplateCode()), line.getTryTimes());
                result.add(msg == null ? (new Message()).setSendFlag(BaseConstants.Flag.YES).setMessageTypeCode("DT") : msg);
            }
        }
    }

    @Override
    public void filterDingTalkReceiver(MessageSender sender) {
        filterReceiver(sender, HmsgConstant.MessageType.DT);
    }

    private boolean c7nSendEnable(List<String> typeCodeList, String typeCode) {
        return CollectionUtils.isEmpty(typeCodeList) || typeCodeList.contains(typeCode);
    }

    public static Boolean isMessageEnabled(Long tenantId, String typeCode) {
        AtomicReference<Boolean> result = new AtomicReference<>();
        SafeRedisHelper.execute(HZeroService.Message.REDIS_DB, helper -> {
            String redisKey = String.format(REDIS_KEY_SYSTEM_MESSAGE, typeCode, tenantId);
            String s = helper.strGet(redisKey);
            if (s != null) {
                result.set(Boolean.parseBoolean(s));
            }
        });
        if (result.get() == null) {
            Boolean messageEnabled = ApplicationContextHelper.getBean(IamFeignClient.class).isMessageEnabled(tenantId, typeCode).getBody();
            result.set(messageEnabled);
        }
        return result.get();
    }

    private void setEmailConfigArgs(MessageSender messageSender, Long tenantId) {
        EmailTemplateConfigDTO configDTO = emailTemplateConfigService.queryConfigByTenantId(tenantId);
        if (!CollectionUtils.isEmpty(messageSender.getObjectArgs())) {
            Map<String, Object> map = messageSender.getObjectArgs();
            map.put(Constants.EmailTemplateConstants.EMAIL_TEMPLATE_LOGO, configDTO.getLogo());
            map.put(Constants.EmailTemplateConstants.EMAIL_TEMPLATE_SLOGAN, configDTO.getSlogan());
            map.put(Constants.EmailTemplateConstants.EMAIL_TEMPLATE_FOOTER, configDTO.getFooter());
        }
        if (!CollectionUtils.isEmpty(messageSender.getArgs())) {
            Map<String, String> map = messageSender.getArgs();
            map.put(Constants.EmailTemplateConstants.EMAIL_TEMPLATE_LOGO, configDTO.getLogo());
            map.put(Constants.EmailTemplateConstants.EMAIL_TEMPLATE_SLOGAN, configDTO.getSlogan());
            map.put(Constants.EmailTemplateConstants.EMAIL_TEMPLATE_FOOTER, configDTO.getFooter());
        }
    }

    private Long getTenantId(MessageSender messageSender) {
        Long tenantId = TenantDTO.DEFAULT_TENANT_ID;
        Optional<Object> projectIdOptional = Optional.ofNullable(messageSender.getAdditionalInformation())
                .map(additionalInformation -> additionalInformation.get(MessageAdditionalType.PARAM_PROJECT_ID.getTypeName()));
        Optional<Object> tenantIdOptional = Optional.ofNullable(messageSender.getAdditionalInformation())
                .map(additionalInformation -> additionalInformation.get(MessageAdditionalType.PARAM_TENANT_ID.getTypeName()));
        if (tenantIdOptional.isPresent()) {
            tenantId = TypeUtils.objToLong(tenantIdOptional.get());
        }
        if (projectIdOptional.isPresent() && tenantId.equals(TenantDTO.DEFAULT_TENANT_ID)) {
            Long projectId = TypeUtils.objToLong(projectIdOptional.get());
            ProjectDTO projectDTO = iamFeignClient.queryProjectByIdWithoutExtraInfo(projectId, false, false, false).getBody();
            if (!ObjectUtils.isEmpty(projectDTO)) {
                tenantId = projectDTO.getOrganizationId();
                messageSender.getAdditionalInformation().put(MessageAdditionalType.PARAM_TENANT_ID.getTypeName(), tenantId);
            }
        }
        return tenantId;
    }
}
