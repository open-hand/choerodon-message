package io.choerodon.notify.api.service;

import io.choerodon.notify.api.dto.NoticeSendDTO;
import io.choerodon.notify.domain.SendSetting;


public interface WebHookService {
    void trySendWebHook(NoticeSendDTO dto, SendSetting sendSetting);
}
