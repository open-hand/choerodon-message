package io.choerodon.notify.api.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.notify.api.dto.SendSettingListDTO;
import io.choerodon.notify.api.dto.SendSettingUpdateDTO;
import io.choerodon.notify.api.service.SendSettingService;
import io.choerodon.notify.domain.SendSetting;
import io.choerodon.notify.infra.mapper.SendSettingMapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SendSettingServiceImpl implements SendSettingService {

    private SendSettingMapper sendSettingMapper;

    private ModelMapper modelMapper;

    public SendSettingServiceImpl(SendSettingMapper sendSettingMapper, ModelMapper modelMapper) {
        this.sendSettingMapper = sendSettingMapper;
        this.modelMapper = modelMapper;
    }

    @Override
    public Set<String> listNames() {
        return sendSettingMapper.selectAll().stream()
                .map(SendSetting::getName).collect(Collectors.toSet());
    }

    @Override
    public Page<SendSettingListDTO> page(final String name, final String code, final String description,
                                         final String params, final PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest,
                () -> sendSettingMapper.fulltextSearch(code, name, description, params));
    }

    @Override
    public SendSetting update(SendSettingUpdateDTO updateDTO) {
        SendSetting db = sendSettingMapper.selectByPrimaryKey(updateDTO.getId());
        if (db == null) {
            throw new CommonException("error.sendSetting.notExist");
        }
        SendSetting dto = modelMapper.map(updateDTO, SendSetting.class);
        dto.setObjectVersionNumber(db.getObjectVersionNumber());
        sendSettingMapper.updateByPrimaryKeySelective(dto);
        return sendSettingMapper.selectByPrimaryKey(updateDTO.getId());
    }
}
