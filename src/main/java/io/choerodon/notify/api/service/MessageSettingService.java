package io.choerodon.notify.api.service;

import io.choerodon.notify.api.dto.MessageSettingVO;

import java.util.List;

/**
 * User: Mr.Wang
 * Date: 2019/12/3
 */
public interface MessageSettingService {
    List<MessageSettingVO> listMessageSetting(Long projectId, MessageSettingVO messageSettingVO);

    void updateMessageSetting(Long projectId, List<MessageSettingVO> messageSettingVOS);

    Long[] checkTargetUser(Long projectId, String ids, String code);
}
