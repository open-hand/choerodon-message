package io.choerodon.notify.api.service;

import io.choerodon.notify.api.dto.EmailConfigDTO;
import io.choerodon.notify.api.dto.EmailSendDTO;
import io.choerodon.notify.api.dto.NoticeSendDTO;
import io.choerodon.notify.api.dto.WsSendDTO;

public interface NoticesSendService {

    void sendEmail(EmailSendDTO dto);

    void sendSiteMessage(WsSendDTO dto);

    void sendNotice(NoticeSendDTO dto);

    void testEmailConnect(EmailConfigDTO config);

}
