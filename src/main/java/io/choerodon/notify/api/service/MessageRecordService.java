package io.choerodon.notify.api.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.notify.api.dto.RecordListDTO;
import io.choerodon.notify.domain.Record;
import org.springframework.data.domain.Pageable;

public interface MessageRecordService {

    PageInfo<RecordListDTO> pageEmail(String status, String receiveEmail, String templateType, String failedReason, String params, Pageable pageable, String level);

    Record manualRetrySendEmail(long recordId);

}
