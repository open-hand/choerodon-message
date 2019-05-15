package io.choerodon.notify.api.service;

import com.github.pagehelper.PageInfo;
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

    PageInfo<SendSettingListDTO> page(String level, String name, String code,
                                      String description, String params, int page, int size);

    PageInfo<SendSettingListDTO> page(String name, String code,
                                  String description, String params, int page, int size);

    SendSetting update(SendSettingUpdateDTO updateDTO);

    SendSettingDetailDTO query(Long id);

    void createByScan(Set<NotifyBusinessTypeScanData> businessTypes);

    List<SendSettingDetailDTO> queryByLevelAndAllowConfig(String level, boolean allowConfig);

    void delete(Long id);
}
