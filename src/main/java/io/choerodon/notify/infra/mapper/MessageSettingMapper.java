package io.choerodon.notify.infra.mapper;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.notify.api.dto.MessageSettingVO;
import io.choerodon.notify.infra.dto.MessageSettingDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MessageSettingMapper extends Mapper<MessageSettingDTO> {

    List<MessageSettingDTO> listMessageSettingByCondition(@Param("projectId") Long projectId, @Param("messageSettingDTO") MessageSettingDTO messageSettingDTO);

    MessageSettingDTO queryByCodeWithoutProjectId(@Param("code") String code);

    List<MessageSettingDTO> queryByCodeOrProjectId(@Param("messageSettingDTO") MessageSettingDTO messageSettingDTO);
}
