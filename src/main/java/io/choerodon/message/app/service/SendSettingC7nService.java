package io.choerodon.message.app.service;

import io.choerodon.message.api.vo.SendSettingVO;

/**
 * @author scp
 * @date 2020/5/7
 * @description
 */
public interface SendSettingC7nService {

    SendSettingVO queryByTempServerId(Long tempServerId);
}
