package io.choerodon.notify.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.notify.api.dto.SiteMsgRecordDTO;
import io.choerodon.notify.api.pojo.SiteMsgRecordQueryParam;
import io.choerodon.notify.domain.SiteMsgRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dengyouquan
 **/
public interface SiteMsgRecordMapper extends BaseMapper<SiteMsgRecord> {

    List<SiteMsgRecordDTO> fulltextSearch(@Param("query") SiteMsgRecordQueryParam siteMsgRecordQueryParam);

    List<SiteMsgRecordDTO> selectByUserIdAndReadAndDeleted(@Param("userId") Long userId,
                                                           @Param("read") Boolean read);

    int selectCountOfUnRead(@Param("userId") Long userId);


}
