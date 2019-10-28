package io.choerodon.notify.api.service;

import io.choerodon.notify.api.dto.NoticeSendDTO;
import io.choerodon.notify.infra.dto.SendSettingDTO;


public interface WebHookService {
    void trySendWebHook(NoticeSendDTO dto, SendSettingDTO sendSetting);
}
