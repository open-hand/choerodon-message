package io.choerodon.message.app.service;

import java.time.LocalDate;

public interface CleanService {
    /**
     * 清理消息监控日志
     *
     * @param configTenantId 清理配置租户ID
     * @param cleanStrategy  清理策略
     */
    void clearLog(Long configTenantId, String cleanStrategy);

    void asyncClearLog(LocalDate localDate, Long tenantId);
}
