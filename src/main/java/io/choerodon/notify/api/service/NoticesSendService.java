package io.choerodon.notify.api.service;

import io.choerodon.notify.api.dto.EmailConfigDTO;
import io.choerodon.notify.api.dto.NoticeSendDTO;
import io.choerodon.notify.infra.dto.NotifyScheduleRecordDTO;

import java.util.Date;

public interface NoticesSendService {

    void sendNotice(NoticeSendDTO dto);

    Long sendScheduleNotice(NoticeSendDTO dto, Date date, String scheduleNoticeCode);

    void testEmailConnect(EmailConfigDTO config);

    void deleteScheduleNotice(NotifyScheduleRecordDTO notifyScheduleRecordDTO);

    void updateScheduleNotice(String scheduleNoticeCode, Date date,  NoticeSendDTO noticeSendDTO, Boolean isNewNotice);
}
