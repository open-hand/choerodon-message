package io.choerodon.notify.api.service;

import io.choerodon.notify.api.dto.MessageSettingCategoryDTO;
import io.choerodon.notify.api.dto.MessageSettingVO;
import io.choerodon.notify.api.vo.CustomMessageSettingVO;
import io.choerodon.notify.api.vo.MessageSettingWarpVO;
import io.choerodon.notify.api.vo.TargetUserVO;
import io.choerodon.notify.infra.dto.MessageSettingDTO;

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

    MessageSettingWarpVO listMessageSettingByType(Long projectId, String notifyType);

    void batchUpdateByType(Long projectId, String notifyType, List<CustomMessageSettingVO> messageSettingVOS);

    void saveMessageSetting(MessageSettingDTO messageSettingDTO);

    MessageSettingVO getSettingByCode(Long projectId, String notifyType, String code, Long envId, String eventName);
}
