package io.choerodon.notify.api.service;

import io.choerodon.notify.api.dto.EmailConfigDTO;
import io.choerodon.notify.api.dto.EmailSendDTO;
import io.choerodon.notify.api.dto.PmSendDTO;

public interface NoticesSendService {

    void sendEmail(EmailSendDTO dto);

    void sendPm(PmSendDTO dto);

    void testEmailConnect(EmailConfigDTO config);

}
