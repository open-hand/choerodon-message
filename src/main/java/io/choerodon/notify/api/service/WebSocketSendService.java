package io.choerodon.notify.api.service;

import java.util.Map;
import java.util.Set;

public interface WebSocketSendService {

    void sendSiteMessage(String code, Map<String, Object> params, Set<Long> ids, Long sendBy);

    void sendWebSocket(String code, String id, String message);

}
