package io.choerodon.notify.api.service;

import io.choerodon.notify.api.dto.EmailConfigDTO;
import io.choerodon.notify.api.dto.NoticeSendDTO;

public interface NoticesSendService {

    void sendNotice(NoticeSendDTO dto);

    void testEmailConnect(EmailConfigDTO config);

}
