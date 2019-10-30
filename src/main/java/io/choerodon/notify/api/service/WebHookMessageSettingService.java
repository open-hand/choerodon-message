package io.choerodon.notify.api.service;

import io.choerodon.notify.infra.dto.WebHookMessageSettingDTO;

import java.util.List;
import java.util.Set;

public interface WebHookMessageSettingService {
    /**
     * 删除WebHook与发送设置的关联
     *
     * @param webHookId WebHook主键
     */
    void deleteByWebHookId(Long webHookId);

    /**
     * 获取WebHook与发送设置的关联
     *
     * @param webHookId WebHook主键
     * @return
     */
    List<WebHookMessageSettingDTO> getByWebHookId(Long webHookId);

    /**
     * 更新WebHook与发送设置的关联
     *
     * @param webHookId
     * @param sendSettingIds
     * @return
     */
    List<WebHookMessageSettingDTO> update(Long webHookId, Set<Long> sendSettingIds);
}
