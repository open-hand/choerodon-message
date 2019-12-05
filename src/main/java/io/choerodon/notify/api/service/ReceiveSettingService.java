package io.choerodon.notify.api.service;

import io.choerodon.notify.api.dto.ReceiveSettingDTO;

import java.util.List;

/**
 * @author dengyouquan
 **/
public interface ReceiveSettingService {
    /**
     * 根据userId查询当前接收配置
     *
     * @param userId
     * @return
     */
    List<ReceiveSettingDTO> queryByUserId(Long userId, String sourceType);

    /**
     * 遍历settingDTOList
     * 删除数据库中对于usrId没有在settingDTOList中对应的记录
     * 插入settingDTOList中没有在数据库中的对应的userId的记录
     * 如果抛出异常，操作会回滚
     *
     * @param settingDTOList settingDTOList是所有当前用户禁用的接收配置
     */
    void update(Long userId, List<ReceiveSettingDTO> settingDTOList);

    /**
     * 删除（disable为false）或插入(disable为true)当前用户sourceId对应组织/项目/平台的通知配置(pm/email)
     * 如果数据库有记录，证明当前用户时禁用了对应的通知配置
     *
     * @param userId
     * @param sourceType
     * @param sourceId
     * @param messageType
     * @param disable
     */
    void updateByUserIdAndSourceTypeAndSourceId(Long userId, String sourceType, Long sourceId, String messageType, boolean disable);

    /**
     * 删除（disable为false）或插入(disable为true)当前用户对应的通知配置
     * 如果数据库有记录，证明当前用户时禁用了对应的通知配置
     *
     * @param userId
     * @param messageType
     * @param disable
     */
    void updateByUserId(Long userId, String messageType, boolean disable);
}
