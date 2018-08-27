package io.choerodon.notify.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.notify.api.dto.RecordListDTO;
import io.choerodon.notify.api.pojo.RecordQueryParam;
import io.choerodon.notify.domain.Record;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RecordMapper extends BaseMapper<Record> {

    List<RecordListDTO> fulltextSearchEmail(@Param("query") final RecordQueryParam param);

    void updateRecordStatus(@Param("id") long id, @Param("status") String status,
                            @Param("reason") String reason, @Param("retryStatus") String retryStatus);
}
