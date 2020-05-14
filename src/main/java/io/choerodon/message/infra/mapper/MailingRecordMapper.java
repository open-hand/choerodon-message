package io.choerodon.message.infra.mapper;


import feign.Param;
import io.choerodon.message.infra.dto.MailingRecordDTO;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;

public interface MailingRecordMapper extends BaseMapper<MailingRecordDTO> {

    List<MailingRecordDTO> selectByDate(@Param("startTime") java.sql.Date startTime,
                                        @Param("endTime") java.sql.Date endTime);

}
