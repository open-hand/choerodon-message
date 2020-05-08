package io.choerodon.message.app.service;

import io.choerodon.message.api.vo.SendSettingVO;
import io.choerodon.message.infra.dto.SendSettingDetailTreeDTO;

import java.util.List;

/**
 * @author scp
 * @date 2020/5/7
 * @description
 */
public interface SendSettingC7nService {

    SendSettingVO queryByTempServerId(Long tempServerId);

    List<SendSettingDetailTreeDTO> queryByLevelAndAllowConfig(String level, boolean allowConfig);
}
