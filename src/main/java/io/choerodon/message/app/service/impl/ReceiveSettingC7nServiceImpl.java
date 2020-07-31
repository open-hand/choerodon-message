package io.choerodon.message.app.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.utils.ConvertUtils;
import io.choerodon.message.api.vo.ReceiveSettingVO;
import io.choerodon.message.app.service.ReceiveSettingC7nService;
import io.choerodon.message.infra.dto.ReceiveSettingDTO;
import io.choerodon.message.infra.mapper.ReceiveSettingC7nMapper;
import io.choerodon.message.infra.validator.CommonValidator;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dengyouquan
 **/
@Component
public class ReceiveSettingC7nServiceImpl implements ReceiveSettingC7nService {
    private final ReceiveSettingC7nMapper receiveSettingC7nMapper;
    private final ModelMapper modelMapper = new ModelMapper();

    public ReceiveSettingC7nServiceImpl(ReceiveSettingC7nMapper receiveSettingC7nMapper) {
        this.receiveSettingC7nMapper = receiveSettingC7nMapper;
        modelMapper.validate();
    }

    @Override
    public List<ReceiveSettingVO> queryByUserId(final Long userId, String sourceType) {
        if (sourceType != null) {
            CommonValidator.validatorLevel(sourceType);
        }
        ReceiveSettingDTO receiveSettingDTO = new ReceiveSettingDTO();
        receiveSettingDTO.setUserId(userId);
        receiveSettingDTO.setSourceType(sourceType);
        return ConvertUtils.convertList(receiveSettingC7nMapper.select(receiveSettingDTO), ReceiveSettingVO.class);
    }

    @Override
    @Transactional
    public void update(final Long userId, final List<ReceiveSettingVO> settingDTOList, String sourceType) {
        if (userId == null) return;
        //没有校验接收通知设置中，层级是否一致，
        // 即sendSettingId中对应的level和接收通知设置中level不一定一致
        List<ReceiveSettingDTO> updateSettings = settingDTOList.stream().
                map(settingDTO -> modelMapper.map(settingDTO, ReceiveSettingDTO.class))
                .peek(receiveSettingDTO -> receiveSettingDTO.setUserId(userId)).collect(Collectors.toList());
        ReceiveSettingDTO receiveSettingDTO = new ReceiveSettingDTO();
        receiveSettingDTO.setUserId(userId);
        receiveSettingDTO.setSourceType(sourceType);
        List<ReceiveSettingDTO> dbSettings = receiveSettingC7nMapper.select(receiveSettingDTO);
        //备份updateSettings，移除updateSettings和数据库dbSettings中不同的元素
        List<ReceiveSettingDTO> insertSetting = new ArrayList<>(updateSettings);
        insertSetting.removeAll(dbSettings);
        //insertSetting是应该插入的元素
        insertSetting.forEach(t -> {
            t.setUserId(userId);
            if (receiveSettingC7nMapper.insert(t) != 1) {
                throw new CommonException("error.receiveSettingDTO.createOrUpdateEmail");
            }
        });
        //移除数据库dbSettings和updateSettings中不同的元素，这些是应该删除的对象
        dbSettings.removeAll(updateSettings);
        dbSettings.forEach(t -> {
            t.setUserId(userId);
            if (receiveSettingC7nMapper.delete(t) != 1) {
                throw new CommonException("error.receiveSettingDTO.createOrUpdateEmail");
            }
        });
    }
}
