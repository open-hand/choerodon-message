package io.choerodon.notify.api.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import freemarker.template.TemplateException;
import io.choerodon.core.exception.CommonException;
import io.choerodon.notify.api.dto.PmRedisMessageDTO;
import io.choerodon.notify.api.dto.PmSendDTO;
import io.choerodon.notify.api.pojo.MessageType;
import io.choerodon.notify.api.service.PmSendService;
import io.choerodon.notify.domain.Template;
import io.choerodon.notify.infra.mapper.TemplateMapper;
import io.choerodon.notify.websocket.MessageSendOperator;
import io.choerodon.notify.websocket.ws.WebSocketPayload;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service("pmWsSendService")
public class PmWsSendServiceImpl implements PmSendService {

    private static final String MSG_TYPE_PM = "pm";

    private final TemplateRender templateRender;

    private final TemplateMapper templateMapper;

    private final MessageSendOperator messageSendOperator;

    public PmWsSendServiceImpl(TemplateRender templateRender,
                               TemplateMapper templateMapper,
                               MessageSendOperator messageSendOperator) {
        this.templateRender = templateRender;
        this.templateMapper = templateMapper;
        this.messageSendOperator = messageSendOperator;
    }

    @Override
    public void send(PmSendDTO dto) {
        Template query = new Template();
        query.setCode(dto.getTemplateCode());
        query.setMessageType(MessageType.PM.getValue());
        Template template = templateMapper.selectOne(query);
        if (template == null) {
            throw new CommonException("error.pmTemplate.notExist");
        }
        try {
            String pm = templateRender.renderTemplate(template, dto.getParams());
            PmRedisMessageDTO data = new PmRedisMessageDTO(dto.getId(), dto.getCode(), pm);
            messageSendOperator.smartSend(new WebSocketPayload<>(MSG_TYPE_PM, data));
        } catch (JsonProcessingException e) {
            throw new CommonException("error.PmSendService.send.JsonProcessingException", e);
        } catch (IOException | TemplateException e) {
            throw new CommonException("error.templateRender.renderError", e);
        }
    }

}
