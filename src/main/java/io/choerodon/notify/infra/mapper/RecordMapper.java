package io.choerodon.notify.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.notify.api.dto.RecordListDTO;
import io.choerodon.notify.domain.Record;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RecordMapper extends BaseMapper<Record> {

    List<RecordListDTO> fulltextSearchEmail(@Param("status") String status, @Param("email") String receiveEmail,
                                            @Param("type") String templateType, @Param("reason") String failedReason,
                                            @Param("level") String level);

    void updateRecordStatus(@Param("id") long id, @Param("status") String status, @Param("reason") String reason);
}
