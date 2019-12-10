package io.choerodon.notify.api.service;

import io.choerodon.notify.api.dto.MessageSettingCategoryDTO;
import io.choerodon.notify.api.dto.MessageSettingVO;
import io.choerodon.notify.api.dto.TargetUserVO;

import java.util.List;

/**
 * User: Mr.Wang
 * Date: 2019/12/3
 */
public interface MessageSettingService {
    List<MessageSettingCategoryDTO> listMessageSetting(Long projectId, MessageSettingVO messageSettingVO);

    void updateMessageSetting(Long projectId, List<MessageSettingVO> messageSettingVOS);

    List<TargetUserVO> getProjectLevelTargetUser(Long projectId, String code);

    MessageSettingVO getMessageSetting(Long projectId, String code);
}
