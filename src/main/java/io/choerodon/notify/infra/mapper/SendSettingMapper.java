package io.choerodon.notify.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.notify.api.dto.SendSettingDetailDTO;
import io.choerodon.notify.api.dto.SendSettingListDTO;
import io.choerodon.notify.domain.SendSetting;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SendSettingMapper extends BaseMapper<SendSetting> {

    List<SendSettingListDTO> fulltextSearch(@Param("level") String level,
                                            @Param("code") String code,
                                            @Param("name") String name,
                                            @Param("description") String description,
                                            @Param("params") String params);

    SendSettingDetailDTO selectById(@Param("id") Long id);

}
