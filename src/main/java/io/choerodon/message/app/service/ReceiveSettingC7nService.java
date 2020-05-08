package io.choerodon.message.app.service;

import java.util.List;

import io.choerodon.message.api.vo.ReceiveSettingVO;

/**
 * @author dengyouquan
 **/
public interface ReceiveSettingC7nService {
    /**
     * 根据userId查询当前接收配置
     *
     * @param userId
     * @return
     */
    List<ReceiveSettingVO> queryByUserId(Long userId, String sourceType);

    /**
     * 遍历settingDTOList
     * 删除数据库中对于usrId没有在settingDTOList中对应的记录
     * 插入settingDTOList中没有在数据库中的对应的userId的记录
     * 如果抛出异常，操作会回滚
     *
     * @param settingDTOList settingDTOList是所有当前用户禁用的接收配置
     */
    void update(Long userId, List<ReceiveSettingVO> settingDTOList, String sourceType);


}
