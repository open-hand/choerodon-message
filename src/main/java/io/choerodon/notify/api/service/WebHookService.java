package io.choerodon.notify.api.service;

import io.choerodon.notify.api.dto.NoticeSendDTO;
import io.choerodon.notify.infra.dto.SendSettingDTO;
import io.choerodon.notify.infra.dto.WebHookDTO;

import java.util.Set;


public interface WebHookService {
    /**
     * 发送WebHook
     *
     * @param noticeSendDTO  发送信息
     * @param sendSettingDTO 发送设置信息
     */
    void trySendWebHook(NoticeSendDTO noticeSendDTO, SendSettingDTO sendSettingDTO);

}
