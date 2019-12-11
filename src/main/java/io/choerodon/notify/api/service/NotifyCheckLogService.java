package io.choerodon.notify.api.service;

/**
 * @author scp
 */
public interface NotifyCheckLogService {
    /**
     * 平滑升级
     *
     * @param version 版本
     */
    void checkLog(String version);
}
