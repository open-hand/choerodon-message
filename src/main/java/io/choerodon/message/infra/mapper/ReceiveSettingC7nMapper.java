package io.choerodon.message.infra.mapper;

import org.apache.ibatis.annotations.Param;

import io.choerodon.message.infra.dto.ReceiveSettingDTO;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * @author dengyouquan
 **/
public interface ReceiveSettingC7nMapper extends BaseMapper<ReceiveSettingDTO> {
    void deleteByUserIdAndSourceTypeAndSourceId(@Param("userId") long userId,
                                                @Param("sourceType") String sourceType,
                                                @Param("sourceId") Long sourceId);
}
