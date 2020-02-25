package io.choerodon.notify.infra.mapper;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.notify.api.dto.RecordListDTO;
import io.choerodon.notify.infra.dto.MailingRecordDTO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface MailingRecordMapper extends Mapper<MailingRecordDTO> {

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

    List<MailingRecordDTO> selectByDate(@Param("startTime") java.sql.Date startTime,
                                        @Param("endTime") java.sql.Date endTime);
}
