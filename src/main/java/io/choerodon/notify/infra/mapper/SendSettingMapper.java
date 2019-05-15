package io.choerodon.notify.infra.mapper;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.notify.api.dto.SendSettingDetailDTO;
import io.choerodon.notify.api.dto.SendSettingListDTO;
import io.choerodon.notify.domain.SendSetting;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SendSettingMapper extends Mapper<SendSetting> {

    List<SendSettingListDTO> fulltextSearch(@Param("level") String level,
                                            @Param("code") String code,
                                            @Param("name") String name,
                                            @Param("description") String description,
                                            @Param("params") String params);

    SendSettingDetailDTO selectById(@Param("id") Long id);

    List<SendSettingDetailDTO> queryByLevelAndAllowConfig(@Param("level") String level,
                                                @Param("allowConfig") boolean allowConfig);

}
