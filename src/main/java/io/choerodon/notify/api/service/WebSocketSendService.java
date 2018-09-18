package io.choerodon.notify.api.service;

import io.choerodon.notify.api.dto.WsSendDTO;

public interface WebSocketSendService {

    void send(WsSendDTO dto);

}
