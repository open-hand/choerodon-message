package io.choerodon.notify.api.service;

import io.choerodon.notify.api.dto.EmailConfigDTO;
import io.choerodon.notify.api.dto.UserDTO;
import io.choerodon.notify.domain.Record;
import io.choerodon.notify.domain.SendSetting;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Map;
import java.util.Set;

public interface EmailSendService {

    void sendEmail(String code, Map<String, Object> params, Set<UserDTO> targetUsers, SendSetting sendSetting);

    void sendRecord(Record record, boolean isManualRetry);

    JavaMailSenderImpl createEmailSender();

    void testEmailConnect(EmailConfigDTO config);

}
