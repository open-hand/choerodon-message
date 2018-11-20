package io.choerodon.notify.api.service;

import io.choerodon.notify.domain.SendSetting;

import java.util.Map;
import java.util.Set;

public interface WebSocketSendService {

    void sendSiteMessage(String code, Map<String, Object> params, Set<Long> ids, Long sendBy, SendSetting sendSetting);

    void sendWebSocket(String code, String id, String message);
}
