package io.choerodon.notify.infra.mapper;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.notify.infra.dto.MessageSettingDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MessageSettingMapper extends Mapper<MessageSettingDTO> {

    List<MessageSettingDTO> listMessageSettingByCondition(@Param("messageSettingDTO") MessageSettingDTO messageSettingDTO);

}
