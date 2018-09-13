package io.choerodon.notify.api.service.impl;

import io.choerodon.notify.api.dto.EmailConfigDTO;
import io.choerodon.notify.api.dto.EmailSendDTO;
import io.choerodon.notify.api.dto.PmSendDTO;
import io.choerodon.notify.api.service.EmailSendService;
import io.choerodon.notify.api.service.NoticesSendService;
import io.choerodon.notify.api.service.PmSendService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class NoticesSendServiceImpl implements NoticesSendService {

    private EmailSendService emailSendService;

    private PmSendService pmSendService;

    public NoticesSendServiceImpl(EmailSendService emailSendService,
                                  @Qualifier("pmWsSendService") PmSendService pmSendService) {
        this.emailSendService = emailSendService;
        this.pmSendService = pmSendService;
    }

    @Override
    public void sendEmail(EmailSendDTO dto) {
      emailSendService.createMailSenderAndSendEmail(dto);
    }

    @Override
    public void sendPm(PmSendDTO dto) {
        pmSendService.send(dto);
    }

    @Override
    public void testEmailConnect(EmailConfigDTO config) {
        emailSendService.testEmailConnect(config);
    }

}
