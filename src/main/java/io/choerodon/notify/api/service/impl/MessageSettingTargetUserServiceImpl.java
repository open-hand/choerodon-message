package io.choerodon.notify.api.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.notify.api.service.MessageSettingTargetUserService;
import io.choerodon.notify.infra.dto.TargetUserDTO;
import io.choerodon.notify.infra.mapper.MessageSettingTargetUserMapper;
import org.springframework.stereotype.Service;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @Date 2019/12/12 17:50
 */
@Service
public class MessageSettingTargetUserServiceImpl implements MessageSettingTargetUserService {

    private static final String ERROR_SAVE_TARGET_USER = "error.save.target.user";
    private static final String ERROR_DELETE_TARGET_USER = "error.delete.target.user";

    private MessageSettingTargetUserMapper messageSettingTargetUserMapper;

    public MessageSettingTargetUserServiceImpl(MessageSettingTargetUserMapper messageSettingTargetUserMapper) {
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
}
