package io.choerodon.message.app.service;

/**
 * Created by wangxiang on 2020/9/15
 */
public interface MessageCheckLogService {
    /**
     * 平滑升级
     *
     * @param version 版本
     */
    void checkLog(String version);
}
