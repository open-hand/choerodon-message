package io.choerodon.notify.api.service.impl;

import freemarker.template.TemplateException;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.FeignException;
import io.choerodon.notify.api.dto.WsSendDTO;
import io.choerodon.notify.api.pojo.MessageType;
import io.choerodon.notify.api.service.WebSocketSendService;
import io.choerodon.notify.domain.SiteMsgRecord;
import io.choerodon.notify.domain.Template;
import io.choerodon.notify.infra.mapper.SiteMsgRecordMapper;
import io.choerodon.notify.infra.mapper.TemplateMapper;
import io.choerodon.notify.websocket.MessageSender;
import io.choerodon.notify.websocket.ws.WebSocketSendPayload;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service("pmWsSendService")
public class WebSocketWsSendServiceImpl implements WebSocketSendService {

    private static final String MSG_TYPE_PM = "sit-msg";

    private final TemplateRender templateRender;

    private final TemplateMapper templateMapper;

    private final SiteMsgRecordMapper siteMsgRecordMapper;

    private final MessageSender messageSender;

    public WebSocketWsSendServiceImpl(TemplateRender templateRender,
                                      TemplateMapper templateMapper,
                                      MessageSender messageSender,
                                      SiteMsgRecordMapper siteMsgRecordMapper) {
        this.templateRender = templateRender;
        this.templateMapper = templateMapper;
        this.messageSender = messageSender;
        this.siteMsgRecordMapper = siteMsgRecordMapper;
    }

    @Override
    public void send(WsSendDTO dto) {
        if (MSG_TYPE_PM.equals(dto.getCode())) {
            sendPmMsg(dto);
        }
    }

    private void sendPmMsg(final WsSendDTO dto) {
        Template query = new Template();
        query.setCode(dto.getTemplateCode());
        query.setMessageType(MessageType.PM.getValue());
        Template template = templateMapper.selectOne(query);
        if (template == null) {
            throw new CommonException("error.pmTemplate.notExist");
        }
        try {
            String pm = templateRender.renderTemplate(template, dto.getParams());
            SiteMsgRecord record = new SiteMsgRecord(dto.getId(), template.getPmTitle(), pm);
            if (siteMsgRecordMapper.insert(record) != 1) {
                throw new FeignException("error.pmSendService.send.siteMsgRecordInsertError");
            }
            String key = "choerodon:msg:sit-msg:" + dto.getId();
            messageSender.sendByKey(key, new WebSocketSendPayload<>(MSG_TYPE_PM, key, siteMsgRecordMapper.selectCountOfUnRead(dto.getId())));
        } catch (IOException | TemplateException e) {
            throw new CommonException("error.templateRender.renderError", e);
        }
    }
}
