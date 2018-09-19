package io.choerodon.notify.api.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.TemplateException;
import io.choerodon.asgard.schedule.annotation.JobParam;
import io.choerodon.asgard.schedule.annotation.JobTask;
import io.choerodon.core.exception.CommonException;
import io.choerodon.notify.api.pojo.MessageType;
import io.choerodon.notify.domain.Template;
import io.choerodon.notify.infra.feign.UserFeignClient;
import io.choerodon.notify.infra.mapper.TemplateMapper;
import io.choerodon.notify.websocket.MessageSender;
import io.choerodon.notify.websocket.ws.WebSocketSendPayload;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dengyouquan
 **/
@Component
public class PmSendTask {
    private static final String MSG_TYPE_PM = "sit-msg";
    public static final String CHOERODON_MSG_SIT_MSG = "choerodon:msg:sit-msg:";
    private final TemplateRender templateRender;
    private final TemplateMapper templateMapper;
    private final MessageSender messageSender;
    private final ObjectMapper objectMapper;
    private final UserFeignClient userFeignClient;

    public PmSendTask(TemplateRender templateRender, TemplateMapper templateMapper, MessageSender messageSender, UserFeignClient userFeignClient) {
        this.templateRender = templateRender;
        this.templateMapper = templateMapper;
        this.messageSender = messageSender;
        this.userFeignClient = userFeignClient;
        objectMapper = new ObjectMapper();
    }

    @JobTask(maxRetryCount = 2, code = "sendPm", params = {
            @JobParam(name = "code", defaultValue = MSG_TYPE_PM),
            @JobParam(name = "templateCode"),
            @JobParam(name = "variables")
    })
    public void sendPm(Map<String, Object> map) {
        String code = (String) map.get("code");
        String templateCode = (String) map.get("templateCode");
        String mapJson = (String) map.get("variables");
        Map<String, Object> params = new HashMap<>(0);
        if(!StringUtils.isEmpty(mapJson)){
            try {
                params = objectMapper.readValue(mapJson, Map.class);
            } catch (IOException e) {
                throw new CommonException("error.pmSendTask.paramsJsonNotValid", e);
            }
        }
        Template template = validatorCode(code, templateCode);
        Map<String, Object> finalParams = params;
        Arrays.stream(userFeignClient.getUserIds().getBody()).map(id -> CHOERODON_MSG_SIT_MSG + id).forEach(key -> {
            try {
                String pm = templateRender.renderPmTemplate(template, finalParams);
                messageSender.sendByKey(key, new WebSocketSendPayload<>(MSG_TYPE_PM, key, pm));
            } catch (IOException | TemplateException e) {
                throw new CommonException("error.templateRender.renderError", e);
            }
        });
    }

    private Template validatorCode(String code, String templateCode) {
        if (!MSG_TYPE_PM.equals(code)) {
            throw new CommonException("error.pmSendTask.codeNotValid");
        }
        if (StringUtils.isEmpty(templateCode)) {
            throw new CommonException("error.pmSendTask.templateCodeEmpty");
        }
        Template query = new Template();
        query.setCode(templateCode);
        query.setMessageType(MessageType.PM.getValue());
        Template template = templateMapper.selectOne(query);
        if (template == null) {
            throw new CommonException("error.pmSendTask.templateCodeNotExist");
        }
        return template;
    }
}
