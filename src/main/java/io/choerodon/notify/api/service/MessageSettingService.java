package io.choerodon.notify.api.service;

import io.choerodon.notify.api.dto.MessageSettingVO;

import java.util.List;

/**
 * User: Mr.Wang
 * Date: 2019/12/3
 */
public interface MessageSettingService {
    List<MessageSettingVO> listMessageSetting(Long projectId, MessageSettingVO messageSettingVO);

    void updateMessageSetting(List<MessageSettingVO> messageSettingVOS);

    Long[] checkTargetUser(Long[] ids, String code);
}
