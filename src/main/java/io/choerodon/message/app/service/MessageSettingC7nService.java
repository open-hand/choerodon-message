package io.choerodon.message.app.service;

import java.util.List;

import io.choerodon.message.api.vo.CustomMessageSettingVO;
import io.choerodon.message.api.vo.MessageSettingVO;
import io.choerodon.message.api.vo.MessageSettingWarpVO;
import io.choerodon.message.infra.dto.MessageSettingDTO;

/**
 * User: Mr.Wang
 * Date: 2019/12/3
 */
public interface MessageSettingC7nService {


    MessageSettingWarpVO listMessageSettingByType(Long projectId, String notifyType, String eventName);

    void batchUpdateByType(Long projectId, String notifyType, List<CustomMessageSettingVO> messageSettingVOS);

    void saveMessageSetting(MessageSettingDTO messageSettingDTO);

    MessageSettingVO getSettingByCode(Long projectId, String notifyType, String code, Long envId, String eventName);

    void deleteByTypeAndEnvId(String type, Long envId);

    void updateMessageSetting(MessageSettingDTO messageSettingDTO);

    void disableNotifyTypeByCodeAndType(String code, String notiyfType);
}
