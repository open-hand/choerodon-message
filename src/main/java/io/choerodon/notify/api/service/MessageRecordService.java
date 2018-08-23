package io.choerodon.notify.api.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.notify.api.dto.RecordListDTO;

public interface MessageRecordService {

    Page<RecordListDTO> pageEmail(PageRequest pageRequest, String status,
                                  String receiveEmail, String templateType,
                                  String failedReason, String params,
                                  String level);

    void manualRetrySendEmail(long recordId);

}
