package io.choerodon.notify.api.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.notify.api.dto.RecordListDTO;
import io.choerodon.notify.api.pojo.RecordQueryParam;
import io.choerodon.notify.domain.Record;

public interface MessageRecordService {

    PageInfo<RecordListDTO> pageEmail(RecordQueryParam param, int page, int size);

    Record manualRetrySendEmail(long recordId);

}
