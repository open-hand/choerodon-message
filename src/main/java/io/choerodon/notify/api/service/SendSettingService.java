package io.choerodon.notify.api.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.notify.api.dto.SendSettingListDTO;
import io.choerodon.notify.api.dto.SendSettingUpdateDTO;
import io.choerodon.notify.domain.SendSetting;

import java.util.Set;

public interface SendSettingService {

    Set<String> listNames();

    Page<SendSettingListDTO> page(String name, String code, String description, String params, PageRequest pageRequest);

    SendSetting update(SendSettingUpdateDTO updateDTO);

}
