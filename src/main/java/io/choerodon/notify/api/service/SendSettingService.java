package io.choerodon.notify.api.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.notify.api.dto.BusinessTypeDTO;
import io.choerodon.notify.api.dto.SendSettingDetailDTO;
import io.choerodon.notify.api.dto.SendSettingListDTO;
import io.choerodon.notify.api.dto.SendSettingUpdateDTO;
import io.choerodon.notify.domain.SendSetting;
import io.choerodon.swagger.notify.NotifyBusinessTypeScanData;

import java.util.List;
import java.util.Set;

public interface SendSettingService {

    Set<BusinessTypeDTO> listNames(String level);

    Set<BusinessTypeDTO> listNames();

    Page<SendSettingListDTO> page(String level, String name, String code,
                                  String description, String params, PageRequest pageRequest);

    Page<SendSettingListDTO> page(String name, String code,
                                  String description, String params, PageRequest pageRequest);

    SendSetting update(SendSettingUpdateDTO updateDTO);

    SendSettingDetailDTO query(Long id);

    void createByScan(Set<NotifyBusinessTypeScanData> businessTypes);

    List<SendSettingDetailDTO> queryByLevelAndAllowConfig(String level, boolean allowConfig);

    void delete(Long id);
}
