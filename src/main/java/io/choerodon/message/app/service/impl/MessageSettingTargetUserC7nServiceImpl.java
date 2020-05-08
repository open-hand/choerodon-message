package io.choerodon.message.app.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import io.choerodon.core.exception.CommonException;
import io.choerodon.message.app.service.MessageSettingTargetUserC7nService;
import io.choerodon.message.infra.dto.TargetUserDTO;
import io.choerodon.message.infra.mapper.MessageSettingTargetUserC7nMapper;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @Date 2019/12/12 17:50
 */
@Service
public class MessageSettingTargetUserC7nServiceImpl implements MessageSettingTargetUserC7nService {

    private static final String ERROR_SAVE_TARGET_USER = "error.save.target.user";
    private static final String ERROR_DELETE_TARGET_USER = "error.delete.target.user";

    private MessageSettingTargetUserC7nMapper messageSettingTargetUserMapper;

    public MessageSettingTargetUserC7nServiceImpl(MessageSettingTargetUserC7nMapper messageSettingTargetUserMapper) {
        this.messageSettingTargetUserMapper = messageSettingTargetUserMapper;
    }

    @Override
    public void save(TargetUserDTO targetUserDTO) {
        if (messageSettingTargetUserMapper.insertSelective(targetUserDTO) != 1) {
            throw new CommonException(ERROR_SAVE_TARGET_USER);
        }
    }

    @Override
    public void deleteBySettingId(Long id) {
        TargetUserDTO targetUserDTO = new TargetUserDTO();
        targetUserDTO.setMessageSettingId(id);
        if (messageSettingTargetUserMapper.delete(targetUserDTO) < 1) {
            throw new CommonException(ERROR_DELETE_TARGET_USER);
        }
    }

    @Override
    public List<TargetUserDTO> getBySettingId(Long id) {
        TargetUserDTO targetUserDTO = new TargetUserDTO();
        targetUserDTO.setMessageSettingId(id);
        return messageSettingTargetUserMapper.select(targetUserDTO);
    }
}
