package io.choerodon.notify.api.service.impl;

import io.choerodon.notify.api.dto.EmailConfigDTO;
import io.choerodon.notify.api.dto.EmailSendDTO;
import io.choerodon.notify.api.dto.WsSendDTO;
import io.choerodon.notify.api.service.EmailSendService;
import io.choerodon.notify.api.service.NoticesSendService;
import io.choerodon.notify.api.service.WebSocketSendService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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
      emailSendService.createMailSenderAndSendEmail(dto);
    }

    @Override
    public void sendWs(WsSendDTO dto) {
        webSocketSendService.send(dto);
    }

    @Override
    public void testEmailConnect(EmailConfigDTO config) {
        emailSendService.testEmailConnect(config);
    }

}
