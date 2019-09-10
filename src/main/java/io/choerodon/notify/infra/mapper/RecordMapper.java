package io.choerodon.notify.infra.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.notify.api.dto.RecordListDTO;
import io.choerodon.notify.api.vo.MessageRecordSearchVO;
import io.choerodon.notify.domain.Record;

public interface RecordMapper extends Mapper<Record> {

    List<RecordListDTO> fulltextSearchEmail(@Param("searchVO") MessageRecordSearchVO searchVO, @Param("param") String param);

    void updateRecordStatusAndIncreaseCount(@Param("id") long id,
                                            @Param("status") String status,
                                            @Param("reason") String reason,
                                            @Param("increase") boolean increase,
                                            @Param("date") Date date);
}
