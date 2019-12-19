package io.choerodon.notify.api.service;

import io.choerodon.notify.api.dto.MessageSettingVO;
import io.choerodon.notify.api.vo.CustomMessageSettingVO;
import io.choerodon.notify.api.vo.MessageSettingWarpVO;
import io.choerodon.notify.infra.dto.MessageSettingDTO;

import java.util.List;

/**
 * User: Mr.Wang
 * Date: 2019/12/3
 */
public interface MessageSettingService {


    MessageSettingWarpVO listMessageSettingByType(Long projectId, String notifyType);

    void batchUpdateByType(Long projectId, String notifyType, List<CustomMessageSettingVO> messageSettingVOS);

    void saveMessageSetting(MessageSettingDTO messageSettingDTO);

    MessageSettingVO getSettingByCode(Long projectId, String notifyType, String code, Long envId, String eventName);

    void deleteByTypeAndEnvId(String type, Long envId);

    void updateMessageSetting(MessageSettingDTO messageSettingDTO);

    void disableNotifyTypeByCodeAndType(String code, String notiyfType);
}
