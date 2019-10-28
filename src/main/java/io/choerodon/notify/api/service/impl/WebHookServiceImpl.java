package io.choerodon.notify.api.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.notify.api.dto.NoticeSendDTO;
import io.choerodon.notify.api.service.WebHookService;
import io.choerodon.notify.infra.dto.SendSettingDTO;
import io.choerodon.notify.infra.dto.Template;
import io.choerodon.notify.infra.dto.WebHookDTO;
import io.choerodon.notify.infra.mapper.TemplateMapper;
import io.choerodon.notify.infra.mapper.WebHookMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class WebHookServiceImpl implements WebHookService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebHookServiceImpl.class);
    private WebHookMapper mapper;
    private TemplateMapper templateMapper;
    private TemplateRender templateRender;

    public WebHookServiceImpl(WebHookMapper mapper, TemplateMapper templateMapper, TemplateRender templateRender) {
        this.mapper = mapper;
        this.templateMapper = templateMapper;
        this.templateRender = templateRender;
    }

    //@Override
    public void trySendWebHook(NoticeSendDTO dto, SendSettingDTO sendSetting) {
        try {
            if (ResourceLevel.PROJECT.value().equals(sendSetting.getLevel()) && dto.getSourceId() != 0) {
                Template template = templateMapper.selectByPrimaryKey(sendSetting.getWhTemplateId());
                validatorPmTemplate(template);
                List<WebHookDTO> hooks = selectWebHookByProjectId(dto.getSourceId());
                for (WebHookDTO hook : hooks) {
                    Map<String, Object> userParams = dto.getParams();
                    String content = templateRender.renderTemplate(template, userParams, TemplateRender.TemplateType.CONTENT);
                    if (WebHookDTO.WEB_HOOK_TYPE_DING_TALK.equals(hook.getWebhookType())) {
                        sendDingTalk(hook, content);
                    } else if (WebHookDTO.WEB_HOOK_TYPE_WE_CHAT.equals(hook.getWebhookType())) {
                        sendWeChat(hook, content);
                    } else if (WebHookDTO.WEB_HOOK_TYPE_JSON.equals(hook.getWebhookType())) {
                        sendJson(hook, dto);
                    } else {
                        throw new CommonException("Unsupport web hook type");
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Web hook send exception {}", e.getMessage());
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
        return mapper.select(example);
    }


    private void validatorPmTemplate(Template template) {
        if (template == null) {
            throw new CommonException("error.whTemplate.notExist");
        }
        if (template.getPmContent() == null) {
            throw new CommonException("error.whTemplate.contentNull");
        }
    }

}
