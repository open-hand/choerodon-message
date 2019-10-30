package io.choerodon.notify.infra.mapper;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.notify.api.dto.MessageServiceVO;
import io.choerodon.notify.api.dto.SendSettingDetailDTO;
import io.choerodon.notify.api.dto.SendSettingListDTO;
import io.choerodon.notify.infra.dto.SendSettingDTO;
import io.choerodon.notify.infra.dto.WebHookMessageSettingDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MessegeSettingMapper extends Mapper<SendSettingDTO> {

    void insertMessage(@Param("webHookMessageSettingDTOs") List<WebHookMessageSettingDTO> webHookMessageSettingDTOs);
}
