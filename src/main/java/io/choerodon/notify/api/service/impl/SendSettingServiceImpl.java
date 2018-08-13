package io.choerodon.notify.api.service.impl;

import io.choerodon.notify.api.service.SendSettingService;
import io.choerodon.notify.domain.SendSetting;
import io.choerodon.notify.infra.mapper.SendSettingMapper;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SendSettingServiceImpl implements SendSettingService {

    private SendSettingMapper sendSettingMapper;

    public SendSettingServiceImpl(SendSettingMapper sendSettingMapper) {
        this.sendSettingMapper = sendSettingMapper;
    }

    @Override
    public Set<String> listNames() {
        return sendSettingMapper.selectAll().stream()
                .map(SendSetting::getName).collect(Collectors.toSet());
    }
}
