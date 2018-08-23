package io.choerodon.notify.api.service;

import io.choerodon.notify.api.dto.EmailConfigDTO;
import io.choerodon.notify.api.dto.EmailSendDTO;
import io.choerodon.notify.domain.Config;
import io.choerodon.notify.domain.Record;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public interface NoticesSendService {

    void createMailSenderAndSendEmail(EmailSendDTO dto);

    void sendEmail(Record record, boolean retry);

    JavaMailSenderImpl createEmailSender(final Config config);

    void testEmailConnect(EmailConfigDTO config);
}
