package io.choerodon.notify.api.service;

import io.choerodon.notify.api.dto.EmailConfigDTO;
import io.choerodon.notify.api.dto.EmailSendDTO;
import io.choerodon.notify.domain.Record;

public interface NoticesSendService {

    void createMailSenderAndSendEmail(EmailSendDTO dto);

    void sendEmail(Record record);

    void testEmailConnect(EmailConfigDTO config);
}
