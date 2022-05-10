package io.choerodon.message.app.service;

import java.util.List;

import io.choerodon.message.api.vo.*;
import io.choerodon.message.app.eventhandler.payload.UserMemberEventPayload;
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

    void asyncMessageProjectUser(List<UserMemberEventPayload> userMemberEventPayloads);

    /**
     * 根据code查询所有开启了发送消息配置（pm/email/webhook）的项目
     *
     * @param code
     * @param notifyType
     * @return
     */
    List<ProjectMessageVO> listEnabledSettingByCode(String code, String notifyType);

    void insertOpenAppConfig(OpenAppVO openAppVO);

    void updateOpenAppConfig(OpenAppVO openAppVO);

    void enableOrDisableOpenAppSyncSetting(OpenAppVO openAppVO);
}
