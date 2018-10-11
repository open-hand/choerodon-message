package io.choerodon.notify.api.service.impl;

import io.choerodon.notify.api.dto.EmailConfigDTO;
import io.choerodon.notify.api.dto.EmailSendDTO;
import io.choerodon.notify.api.dto.NoticeSendDTO;
import io.choerodon.notify.api.dto.WsSendDTO;
import io.choerodon.notify.api.service.EmailSendService;
import io.choerodon.notify.api.service.NoticesSendService;
import io.choerodon.notify.api.service.WebSocketSendService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NoticesSendServiceImpl implements NoticesSendService {

    private EmailSendService emailSendService;

    private WebSocketSendService webSocketSendService;

    public NoticesSendServiceImpl(EmailSendService emailSendService,
                                  @Qualifier("pmWsSendService") WebSocketSendService webSocketSendService) {
        this.emailSendService = emailSendService;
        this.webSocketSendService = webSocketSendService;
    }

    @Override
    public void sendEmail(EmailSendDTO dto) {
        emailSendService.sendEmail(dto.getCode(), dto.getVariables(), Collections.singleton(dto.getDestinationEmail()));
    }

    @Override
    public void sendWs(WsSendDTO dto) {
        webSocketSendService.send(dto.getCode(), dto.getParams(), Collections.singleton(dto.getId()));
    }

    @Override
    public void testEmailConnect(EmailConfigDTO config) {
        emailSendService.testEmailConnect(config);
    }

    @Override
    public void sendNotice(NoticeSendDTO dto) {
        Set<String> emails = dto.getTargetUsers().stream().map(NoticeSendDTO.User::getEmail).collect(Collectors.toSet());
        emailSendService.sendEmail(dto.getCode(), dto.getParams(), emails);
        Set<Long> ids = dto.getTargetUsers().stream().map(NoticeSendDTO.User::getId).collect(Collectors.toSet());
        webSocketSendService.send(dto.getCode(), dto.getParams(), ids);
    }
}
