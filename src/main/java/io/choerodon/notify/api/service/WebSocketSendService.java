package io.choerodon.notify.api.service;

import java.util.Map;
import java.util.Set;

public interface WebSocketSendService {

    void send(String code, Map<String, Object> params, Set<Long> ids);

}
