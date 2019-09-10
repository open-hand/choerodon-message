package io.choerodon.notify.api.service;

import com.github.pagehelper.PageInfo;

import io.choerodon.base.domain.PageRequest;
import io.choerodon.notify.api.dto.RecordListDTO;
import io.choerodon.notify.api.vo.MessageRecordSearchVO;
import io.choerodon.notify.domain.Record;

public interface MessageRecordService {

    PageInfo<RecordListDTO> pageEmail(PageRequest pageRequest, MessageRecordSearchVO searchVO);

    Record manualRetrySendEmail(long recordId);

}
