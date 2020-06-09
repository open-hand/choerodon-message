package io.choerodon.notify.api.service.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ext.InsertException;
import io.choerodon.core.exception.ext.NotExistedException;
import io.choerodon.core.exception.ext.UpdateException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.notify.api.dto.NoticeSendDTO;
import io.choerodon.notify.api.service.SendSettingService;
import io.choerodon.notify.api.service.TemplateService;
import io.choerodon.notify.api.service.WebHookMessageSettingService;
import io.choerodon.notify.api.service.WebHookService;
import io.choerodon.notify.api.vo.WebHookVO;
import io.choerodon.notify.infra.dto.*;
import io.choerodon.notify.infra.enums.RecordStatus;
import io.choerodon.notify.infra.enums.SendingTypeEnum;
import io.choerodon.notify.infra.enums.WebHookTypeEnum;
import io.choerodon.notify.infra.mapper.WebHookMapper;
import io.choerodon.notify.infra.mapper.WebhookRecordMapper;
import io.choerodon.web.util.PageableHelper;

@Service
public class WebHookServiceImpl implements WebHookService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebHookServiceImpl.class);
    private WebHookMapper webHookMapper;
    private WebHookMessageSettingService webHookMessageSettingService;
    private TemplateService templateService;
    private SendSettingService sendSettingService;
    private TemplateRender templateRender;
    private WebhookRecordMapper webhookRecordMapper;

    public WebHookServiceImpl(WebHookMapper webHookMapper, WebHookMessageSettingService webHookMessageSettingService, TemplateService templateService, SendSettingService sendSettingService, TemplateRender templateRender, WebhookRecordMapper webhookRecordMapper) {
        this.webHookMapper = webHookMapper;
        this.webHookMessageSettingService = webHookMessageSettingService;
        this.templateService = templateService;
        this.sendSettingService = sendSettingService;
        this.templateRender = templateRender;
        this.webhookRecordMapper = webhookRecordMapper;
    }

    @Override
    public void trySendWebHook(NoticeSendDTO dto, SendSettingDTO sendSetting, Set<String> mobiles) {
        LOGGER.info(">>>START_SENDING_WEB_HOOK>>> Send a web hook.[INFO:send_setting_code:'{}']", sendSetting.getCode());
        //0. 若发送设置非项目层 / 发送信息未指定项目Id 则取消发送
        if (!ResourceLevel.PROJECT.value().equalsIgnoreCase(sendSetting.getLevel())
                || ObjectUtils.isEmpty(dto.getSourceId())
                || dto.getSourceId().equals(0L)) {
            LOGGER.warn(">>>CANCEL_SENDING_WEBHOOK>>> Missing project information.");
            return;
        }

        //1. 获取该发送设置的WebHook模版
        Template template = null;
        try {
            template = templateService.getOne(new Template()
                    .setSendingType(SendingTypeEnum.WH.getValue())
                    .setSendSettingCode(sendSetting.getCode()));
        } catch (Exception e) {
            LOGGER.warn(">>>CANCEL_SENDING_WEBHOOK>>> No valid templates available.", e);
            return;
        }

        //2. 获取项目下配置该发送设置的WebHook
        Set<WebHookDTO> hooks = webHookMapper.selectBySendSetting(dto.getSourceId(), sendSetting.getId());
        if (CollectionUtils.isEmpty(hooks)) {
            LOGGER.info(">>>CANCEL_SENDING_WEBHOOK>>> The send settings have not been associated with webhook.");
            return;
        }
        //3. 发送WebHook
        WebhookRecordDTO webhookRecordDTO = new WebhookRecordDTO();
        try {
            for (WebHookDTO hook : hooks) {

                Map<String, Object> userParams = dto.getParams();
                if (userParams.containsKey("url") && !StringUtils.isEmpty(userParams.get("url"))) {
                    userParams.put("url", ChineseToUrls(userParams.get("url").toString()));
                }
                String content = templateRender.renderTemplate(template, userParams, TemplateRender.TemplateType.CONTENT);
                if (WebHookTypeEnum.DINGTALK.getValue().equalsIgnoreCase(hook.getType())) {
                    webhookRecordDTO.setWebhookPath(hook.getWebhookPath());
                    webhookRecordDTO.setProjectId(hook.getProjectId());
                    webhookRecordDTO.setContent(content);
                    webhookRecordDTO.setSendSettingCode(dto.getCode());
                    String title = templateRender.renderTemplate(template, userParams, TemplateRender.TemplateType.TITLE);
                    sendDingTalk(hook, content, title, mobiles, dto.getCode());
                } else if (WebHookTypeEnum.WECHAT.getValue().equalsIgnoreCase(hook.getType())) {
                    webhookRecordDTO.setWebhookPath(hook.getWebhookPath());
                    webhookRecordDTO.setProjectId(hook.getProjectId());
                    webhookRecordDTO.setContent(content);
                    webhookRecordDTO.setSendSettingCode(dto.getCode());
                    sendWeChat(hook, content, dto.getCode());
                } else if (WebHookTypeEnum.JSON.getValue().equalsIgnoreCase(hook.getType())) {
                    webhookRecordDTO.setWebhookPath(hook.getWebhookPath());
                    webhookRecordDTO.setProjectId(hook.getProjectId());
                    webhookRecordDTO.setContent(content);
                    webhookRecordDTO.setSendSettingCode(dto.getCode());
                    sendJson(hook, dto);
                } else {
                    throw new CommonException("Unsupported web hook type : {}", hook.getType());
                }
            }
        } catch (Exception e) {
            webhookRecordDTO.setStatus(RecordStatus.FAILED.getValue());
            webhookRecordDTO.setFailedReason(e.getMessage());
            webhookRecordMapper.insertSelective(webhookRecordDTO);
            LOGGER.error(">>>SENDING_WEBHOOK_ERROR>>> An error occurred while sending the web hook", e.getMessage());
        }
    }


    /**
     * 触发钉钉的 WebHook 机器人
     * 钉钉的 WebHook 自定义机器人的配置文档：https://ding-doc.dingtalk.com/doc#/serverapi3/iydd5h
     *
     * @param hook  WebHook 配置
     * @param text  发送内容
     * @param title 发送主题
     */
    private void sendDingTalk(WebHookDTO hook, String text, String title, Set<String> mobiles, String code) {
        RestTemplate template = new RestTemplate();
        WebhookRecordDTO webhookRecordDTO = new WebhookRecordDTO();
        webhookRecordDTO.setWebhookPath(hook.getWebhookPath());
        webhookRecordDTO.setProjectId(hook.getProjectId());
        webhookRecordDTO.setContent(text);
        webhookRecordDTO.setSendSettingCode(code);
        try {
            //1.添加安全设置，构造请求uri（此处直接封装uri而非用String类型来进行http请求：RestTemplate 在执行请求时，如果路径为String类型，将分析路径参数并组合路径，此时会丢失sign的部分特殊字符）
            long timestamp = System.currentTimeMillis();
            URI uri = new URI(hook.getWebhookPath() + "&timestamp=" + timestamp + "&sign=" + addSignature(hook.getSecret(), timestamp));
            //2.添加发送类型
            Map<String, Object> request = new HashMap<>();
            request.put("msgtype", "markdown");
            //3.添加@对象
            Map<String, Object> at = new HashMap<>();
            at.put("isAtAll", CollectionUtils.isEmpty(mobiles));
            if (!CollectionUtils.isEmpty(mobiles)) {
                at.put("atMobiles", mobiles);
            }
            request.put("at", at);
            for (String mobile : mobiles) {
                text = "@" + mobile + text;
            }
            //4.添加发送内容
            Map<String, Object> markdown = new HashMap<>();
            markdown.put("text", text);
            markdown.put("title", title);
            request.put("markdown", markdown);
            //5.发送请求
            ResponseEntity<String> response = template.postForEntity(uri, request, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                LOGGER.warn(">>>SENDING_WEBHOOK_ERROR>>> Sending the web hook was not successful,response:{}", response);
                webhookRecordDTO.setStatus(RecordStatus.FAILED.getValue());
                webhookRecordDTO.setFailedReason(response.getBody());
                webhookRecordMapper.insertSelective(webhookRecordDTO);
            } else {
                webhookRecordDTO.setStatus(RecordStatus.COMPLETE.getValue());
                webhookRecordMapper.insertSelective(webhookRecordDTO);
            }
        } catch (URISyntaxException e) {
            webhookRecordDTO.setStatus(RecordStatus.FAILED.getValue());
            webhookRecordDTO.setFailedReason(e.getMessage());
            webhookRecordMapper.insertSelective(webhookRecordDTO);
            e.printStackTrace();
        }


    }

    /**
     * 钉钉加签方法
     * 第一步，把timestamp+"\n"+密钥当做签名字符串，使用HmacSHA256算法计算签名，然后进行Base64 encode，最后再把签名参数再进行urlEncode，得到最终的签名（需要使用UTF-8字符集）
     *
     * @param secret
     * @param timestamp
     * @return
     */
    private String addSignature(String secret, Long timestamp) {
        //第一步，把timestamp+"\n"+密钥当做签名字符串
        String stringToSign = timestamp + "\n" + secret;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            return URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
        } catch (Exception e) {
            LOGGER.error(">>>SENDING_WEBHOOK_ERROR>>> An error occurred while adding the signature", e.getMessage());
            return null;
        }
    }

    private void sendWeChat(WebHookDTO hook, String content, String code) {
        RestTemplate template = new RestTemplate();
        Map<String, Object> request = new TreeMap<>();
        request.put("msgtype", "markdown");
        Map<String, Object> markdown = new TreeMap<>();
        markdown.put("content", content);
        request.put("markdown", markdown);
        ResponseEntity<String> response = template.postForEntity(hook.getWebhookPath(), request, String.class);
        WebhookRecordDTO webhookRecordDTO = new WebhookRecordDTO();
        webhookRecordDTO.setWebhookPath(hook.getWebhookPath());
        webhookRecordDTO.setProjectId(hook.getProjectId());
        webhookRecordDTO.setContent(content);
        webhookRecordDTO.setSendSettingCode(code);
        if (!response.getStatusCode().is2xxSuccessful()) {
            webhookRecordDTO.setStatus(RecordStatus.FAILED.getValue());
            webhookRecordDTO.setFailedReason(response.getBody());
            webhookRecordMapper.insertSelective(webhookRecordDTO);
            LOGGER.warn("Web hook response not success {}", response);
        } else {
            webhookRecordDTO.setStatus(RecordStatus.COMPLETE.getValue());
            webhookRecordMapper.insertSelective(webhookRecordDTO);
        }
    }

    private void sendJson(WebHookDTO hook, NoticeSendDTO dto) {
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> response = template.postForEntity(hook.getWebhookPath(), dto, String.class);
        WebhookRecordDTO webhookRecordDTO = new WebhookRecordDTO();
        webhookRecordDTO.setWebhookPath(hook.getWebhookPath());
        webhookRecordDTO.setProjectId(hook.getProjectId());
        webhookRecordDTO.setSendSettingCode(dto.getCode());
        if (!response.getStatusCode().is2xxSuccessful()) {
            LOGGER.warn("Web hook response not success {}", response);
            webhookRecordDTO.setStatus(RecordStatus.FAILED.getValue());
            webhookRecordDTO.setFailedReason(response.getBody());
            webhookRecordMapper.insertSelective(webhookRecordDTO);
        } else {
            webhookRecordDTO.setStatus(RecordStatus.COMPLETE.getValue());
            webhookRecordMapper.insertSelective(webhookRecordDTO);
        }
    }

    private List<WebHookDTO> selectWebHookByProjectId(Long projectId) {
        WebHookDTO example = new WebHookDTO();
        example.setProjectId(projectId);
        return webHookMapper.select(example);
    }

    @Override
    public PageInfo<WebHookDTO> pagingWebHook(Pageable pageable, Long projectId, WebHookDTO filterDTO, String params) {
        return PageHelper.startPage(pageable.getPageNumber(), pageable.getPageSize(), PageableHelper.getSortSql(pageable.getSort()))
                .doSelectPageInfo(() -> webHookMapper.doFTR(projectId, filterDTO, params));
    }

    @Override
    public Boolean checkPath(Long id, String path) {
        if (StringUtils.isEmpty(path)) {
            throw new CommonException("error.web.hook.check.path.can.not.be.empty");
        }
        WebHookDTO existDTO = webHookMapper.selectOne(new WebHookDTO().setWebhookPath(path));
        return ObjectUtils.isEmpty(existDTO)
                || (!ObjectUtils.isEmpty(existDTO) && existDTO.getId().equals(id));
    }

    @Override
    public WebHookVO getById(Long projectId, Long id) {
        //1.查询WebHookVO
        WebHookDTO webHookDTO = checkExistedById(id);
        WebHookVO webHookVO = new WebHookVO();
        BeanUtils.copyProperties(webHookDTO, webHookVO);
        //2.查询可选的发送设置
        webHookVO.setTriggerEventSelection(sendSettingService.getUnderProject());
        //3.查询已选的发送设置主键
        List<WebHookMessageSettingDTO> byWebHookId = webHookMessageSettingService.getByWebHookId(id);
        webHookVO.setSendSettingIdList(CollectionUtils.isEmpty(byWebHookId) ? null : byWebHookId.stream().map(WebHookMessageSettingDTO::getSendSettingId).collect(Collectors.toSet()));
        return webHookVO;
    }

    @Override
    @Transactional
    public WebHookVO create(Long projectId, WebHookVO webHookVO) {
        //0.校验web hook path
        if (!checkPath(null, webHookVO.getWebhookPath())) {
            throw new CommonException("error.web.hook.path.duplicate");
        }
        //1.新增WebHook
        if (webHookMapper.insertSelective(webHookVO) != 1) {
            throw new InsertException("error.web.hook.insert");
        }
        //2.新增WebHook的发送设置配置
        webHookMessageSettingService.update(webHookVO.getId(), webHookVO.getSendSettingIdList());
        //3.返回数据
        return getById(projectId, webHookVO.getId());
    }

    @Override
    @Transactional
    public WebHookVO update(Long projectId, WebHookVO webHookVO) {
        //0.校验web hook path
        if (!checkPath(webHookVO.getId(), webHookVO.getWebhookPath())) {
            throw new CommonException("error.web.hook.path.duplicate");
        }
        //1.更新WebHook
        WebHookDTO webHookDTO = checkExistedById(webHookVO.getId());
        webHookDTO.setObjectVersionNumber(webHookVO.getObjectVersionNumber());
        if (webHookMapper.updateByPrimaryKeySelective(webHookDTO
                .setSecret(webHookVO.getSecret())
                .setName(webHookVO.getName())
                .setType(webHookVO.getType())
                .setWebhookPath(webHookVO.getWebhookPath())) != 1) {
            throw new UpdateException("error.web.hook.update");
        }
        //2.更新WebHook的发送设置配置
        webHookMessageSettingService.update(webHookDTO.getId(), webHookVO.getSendSettingIdList());
        //3.返回更新数据
        return getById(projectId, webHookDTO.getId());
    }


    @Override
    @Transactional
    public void delete(Long id) {
        //1.如 WebHook 不存在，则取消删除
        try {
            checkExistedById(id);
        } catch (NotExistedException e) {
            return;
        }
        //2.删除 WebHook
        if (webHookMapper.deleteByPrimaryKey(id) != 1) {
            throw new CommonException("error.web.hook.delete");
        }
        //3.删除 WebHook Message
        webHookMessageSettingService.deleteByWebHookId(id);
    }

    @Override
    public WebHookDTO disabled(Long id) {
        WebHookDTO webHookDTO = checkExistedById(id);
        if (webHookDTO.getEnableFlag() && webHookMapper.updateByPrimaryKeySelective(webHookDTO.setEnableFlag(false)) != 1) {
            throw new UpdateException("error.web.hook.disabled");
        }
        return webHookDTO;
    }

    @Override
    public WebHookDTO enabled(Long id) {
        WebHookDTO webHookDTO = checkExistedById(id);
        if (!webHookDTO.getEnableFlag() && webHookMapper.updateByPrimaryKeySelective(webHookDTO.setEnableFlag(true)) != 1) {
            throw new UpdateException("error.web.hook.enabled");
        }
        return webHookDTO;
    }

    /**
     * 根据主键校验WebHook是否存在
     *
     * @param id WebHook主键
     * @return WebHook
     */
    private WebHookDTO checkExistedById(Long id) {
        return Optional.ofNullable(webHookMapper.selectByPrimaryKey(id))
                .orElseThrow(() -> new NotExistedException("error.web.hook.does.not.existed"));
    }



    /**
     * 汉字转换成URL码
     */
    public static String ChineseToUrls(String s) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= 0 && c <= 255) {
                sb.append(c);
            } else {
                byte[] b;
                try {
                    //指定需要的编码类型
                    b = String.valueOf(c).getBytes("utf-8");
                } catch (Exception ex) {
                    System.out.println(ex);
                    b = new byte[0];
                }
                for (int j = 0; j < b.length; j++) {
                    int k = b[j];
                    if (k < 0)
                        k += 256;
                    sb.append("%" + Integer.toHexString(k).toUpperCase());
                }
            }
        }
        return sb.toString();
    }
}
