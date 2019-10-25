package io.choerodon.notify.infra.mapper;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.notify.api.dto.SiteMsgRecordDTO;
import io.choerodon.notify.api.pojo.SiteMsgRecordQueryParam;
import io.choerodon.notify.infra.dto.SiteMsgRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dengyouquan
 **/
public interface SiteMsgRecordMapper extends Mapper<SiteMsgRecord> {

    List<SiteMsgRecordDTO> fulltextSearch(@Param("query") SiteMsgRecordQueryParam siteMsgRecordQueryParam);

    List<SiteMsgRecordDTO> selectByUserIdAndReadAndDeleted(@Param("userId") Long userId,
                                                           @Param("read") Boolean read,
                                                           @Param("type") String type);

    int selectCountOfUnRead(@Param("userId") Long userId);

    int batchInsert(@Param("records") List<SiteMsgRecord> records);
}
