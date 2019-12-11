package io.choerodon.notify.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.notify.infra.dto.TargetUserDTO;


public interface MessageSettingTargetUserMapper extends Mapper<TargetUserDTO> {

    List<TargetUserDTO> listByMsgSettingId(@Param("msgSettingId") Long msgSettingId);
}
