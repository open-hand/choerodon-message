package io.choerodon.notify.infra.mapper;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.notify.infra.dto.ReceiveSettingDTO;
import org.apache.ibatis.annotations.Param;

/**
 * @author dengyouquan
 **/
public interface ReceiveSettingMapper extends Mapper<ReceiveSettingDTO> {
    void deleteByUserIdAndSourceTypeAndSourceId(@Param("userId") long userId,
                                                @Param("sourceType") String sourceType,
                                                @Param("sourceId") Long sourceId);
}
