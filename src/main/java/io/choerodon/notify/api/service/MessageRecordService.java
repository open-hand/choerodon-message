package io.choerodon.notify.api.service;

import com.github.pagehelper.PageInfo;

import org.springframework.data.domain.*;
import io.choerodon.notify.api.dto.*;
import io.choerodon.notify.domain.*;

public interface MessageRecordService {

    PageInfo<RecordListDTO> pageEmail(String status, String receiveEmail, String templateType, String failedReason, String params, Pageable pageable, String level);

    Record manualRetrySendEmail(long recordId);

}
