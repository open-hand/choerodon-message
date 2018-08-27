package io.choerodon.notify.api.service;

import io.choerodon.core.domain.Page;
import io.choerodon.notify.api.dto.RecordListDTO;
import io.choerodon.notify.api.pojo.RecordQueryParam;
import io.choerodon.notify.domain.Record;

public interface MessageRecordService {

    Page<RecordListDTO> pageEmail(RecordQueryParam param);

    Record manualRetrySendEmail(long recordId);

}
