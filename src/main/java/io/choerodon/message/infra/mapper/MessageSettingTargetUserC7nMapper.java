package io.choerodon.message.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.message.infra.dto.TargetUserDTO;
import io.choerodon.mybatis.common.BaseMapper;


public interface MessageSettingTargetUserC7nMapper extends BaseMapper<TargetUserDTO> {

    List<TargetUserDTO> listByMsgSettingId(@Param("msgSettingId") Long msgSettingId);
}
