package io.choerodon.notify.api.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.notify.api.dto.BusinessTypeDTO;
import io.choerodon.notify.api.dto.SendSettingDetailDTO;
import io.choerodon.notify.api.dto.SendSettingListDTO;
import io.choerodon.notify.api.dto.SendSettingUpdateDTO;
import io.choerodon.notify.api.service.SendSettingService;
import io.choerodon.notify.domain.SendSetting;
import io.choerodon.notify.infra.mapper.SendSettingMapper;
import io.choerodon.notify.infra.utils.ConvertUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SendSettingServiceImpl implements SendSettingService {

    private SendSettingMapper sendSettingMapper;

    private final ModelMapper modelMapper = new ModelMapper();

    public SendSettingServiceImpl(SendSettingMapper sendSettingMapper) {
        this.sendSettingMapper = sendSettingMapper;
    }

    @Override
    public Set<BusinessTypeDTO> listNames(final String level) {
        SendSetting query = new SendSetting();
        query.setLevel(level);
        return sendSettingMapper.select(query).stream()
                .map(ConvertUtils::convertBusinessTypeDTO).collect(Collectors.toSet());
    }

    @Override
    public Page<SendSettingListDTO> page(final String level, final String name, final String code,
                                         final String description, final String params, final PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest,
                () -> sendSettingMapper.fulltextSearch(level, code, name, description, params));
    }

    @Override
    public SendSetting update(SendSettingUpdateDTO updateDTO) {
        SendSetting db = sendSettingMapper.selectByPrimaryKey(updateDTO.getId());
        if (db == null) {
            throw new CommonException("error.sendSetting.notExist");
        }
        SendSetting dto = modelMapper.map(updateDTO, SendSetting.class);
        sendSettingMapper.updateByPrimaryKeySelective(dto);
        return sendSettingMapper.selectByPrimaryKey(updateDTO.getId());
    }

    @Override
    public SendSettingDetailDTO query(Long id) {
        SendSettingDetailDTO sendSetting = sendSettingMapper.selectById(id);
        if (sendSetting == null) {
            throw new CommonException("error.sendSetting.notExist");
        }
        return sendSetting;
    }
}
