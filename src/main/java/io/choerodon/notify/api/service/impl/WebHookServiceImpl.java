package io.choerodon.notify.api.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.notify.api.dto.NoticeSendDTO;
import io.choerodon.notify.api.service.TemplateService;
import io.choerodon.notify.api.service.WebHookService;
import io.choerodon.notify.infra.dto.SendSettingDTO;
import io.choerodon.notify.infra.dto.Template;
import io.choerodon.notify.infra.dto.WebHookDTO;
import io.choerodon.notify.infra.enums.SendingTypeEnum;
import io.choerodon.notify.infra.enums.WebHookTypeEnum;
import io.choerodon.notify.infra.mapper.WebHookMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Service
public class WebHookServiceImpl implements WebHookService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebHookServiceImpl.class);
    private WebHookMapper webHookMapper;
    private TemplateService templateService;
    private TemplateRender templateRender;


    public WebHookServiceImpl(WebHookMapper webHookMapper, TemplateService templateService, TemplateRender templateRender) {
        this.webHookMapper = webHookMapper;
        this.templateService = templateService;
        this.templateRender = templateRender;
    }

    @Override
    public void trySendWebHook(NoticeSendDTO dto, SendSettingDTO sendSetting) {
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
            LOGGER.warn(">>>CANCEL_SENDING_WEBHOOK>>> No valid templates available.");
            return;
        }

        //2. 获取项目下配置该发送设置的WebHook
        Set<WebHookDTO> hooks = webHookMapper.selectBySendSetting(dto.getSourceId(), sendSetting.getId());
        if (CollectionUtils.isEmpty(hooks)) {
            LOGGER.info(">>>CANCEL_SENDING_WEBHOOK>>> The send settings have not been associated with webhook.");
            return;
        }
        //3. 发送WebHook
        try {
            for (WebHookDTO hook : hooks) {
                Map<String, Object> userParams = dto.getParams();
                String content = templateRender.renderTemplate(template, userParams, TemplateRender.TemplateType.CONTENT);
                if (WebHookTypeEnum.DINGTALK.getValue().equalsIgnoreCase(hook.getType())) {
                    sendDingTalk(hook, content);
                } else if (WebHookTypeEnum.WECHAT.getValue().equalsIgnoreCase(hook.getType())) {
                    sendWeChat(hook, content);
                } else if (WebHookTypeEnum.JSON.getValue().equalsIgnoreCase(hook.getType())) {
                    sendJson(hook, dto);
                } else {
                    throw new CommonException("Unsupported web hook type : {}", hook.getType());
                }
            }
        } catch (Exception e) {
            LOGGER.error(">>>SENDING_WEBHOOK_ERROR>>> An error occurred while sending the web hook", e.getMessage());
        }
    }

    private void sendDingTalk(WebHookDTO hook, String content) {
        RestTemplate template = new RestTemplate();
        Map<String, Object> request = new TreeMap<>();
        request.put("msgtype", "text");
        Map<String, Object> text = new TreeMap<>();
        text.put("content", content);
        request.put("text", text);
        ResponseEntity<String> response = template.postForEntity(hook.getWebhookPath(), request, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            LOGGER.warn("Web hook response not success {}", response);
        }
    }

    private void sendWeChat(WebHookDTO hook, String content) {
        RestTemplate template = new RestTemplate();
        Map<String, Object> request = new TreeMap<>();
        request.put("msgtype", "text");
        Map<String, Object> text = new TreeMap<>();
        text.put("content", content);
        request.put("text", text);
        ResponseEntity<String> response = template.postForEntity(hook.getWebhookPath(), request, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            LOGGER.warn("Web hook response not success {}", response);
        }
    }

    private void sendJson(WebHookDTO hook, NoticeSendDTO dto) {
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> response = template.postForEntity(hook.getWebhookPath(), dto, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            LOGGER.warn("Web hook response not success {}", response);
        }
    }

    private List<WebHookDTO> selectWebHookByProjectId(Long projectId) {
        WebHookDTO example = new WebHookDTO();
        example.setProjectId(projectId);
        return webHookMapper.select(example);
    }
}
