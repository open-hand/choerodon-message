package io.choerodon.notify.api.service;

import io.choerodon.notify.api.dto.EmailConfigDTO;
import io.choerodon.notify.api.dto.NoticeSendDTO;
import java.util.Date;

public interface NoticesSendService {

    void sendNotice(NoticeSendDTO dto);

    Long sendScheduleNotice(NoticeSendDTO dto, Date date);

    void testEmailConnect(EmailConfigDTO config);

}
