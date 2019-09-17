package io.choerodon.notify.api.service;

import com.github.pagehelper.PageInfo;

import io.choerodon.base.domain.*;
import io.choerodon.notify.api.dto.*;
import io.choerodon.notify.domain.*;

public interface MessageRecordService {

    PageInfo<RecordListDTO> pageEmail(String status, String receiveEmail, String templateType, String failedReason, String params, PageRequest pageRequest, String level);

    Record manualRetrySendEmail(long recordId);

}
