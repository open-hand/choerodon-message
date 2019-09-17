package io.choerodon.notify.infra.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.*;

import io.choerodon.mybatis.common.*;
import io.choerodon.notify.api.dto.*;
import io.choerodon.notify.domain.*;

public interface RecordMapper extends Mapper<Record> {

    List<RecordListDTO> fulltextSearchEmail(@Param("status") String status,
                                            @Param("receiveEmail") String receiveEmail,
                                            @Param("templateType") String templateType,
                                            @Param("failedReason") String failedReason,
                                            @Param("params") String params,
                                            @Param("level") String level);

    void updateRecordStatusAndIncreaseCount(@Param("id") long id,
                                            @Param("status") String status,
                                            @Param("reason") String reason,
                                            @Param("increase") boolean increase,
                                            @Param("date") Date date);
}
