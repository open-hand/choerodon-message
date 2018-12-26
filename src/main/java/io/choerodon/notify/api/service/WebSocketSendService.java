package io.choerodon.notify.api.service;

import java.util.Map;
import java.util.Set;

import io.choerodon.notify.api.dto.UserDTO;
import io.choerodon.notify.domain.SendSetting;

public interface WebSocketSendService {

    void sendSiteMessage(String code, Map<String, Object> params, Set<UserDTO> targetUsers, Long sendBy, String senderType, SendSetting sendSetting);

    void sendWebSocket(String code, String id, String message);

    void sendVisitorsInfo(Integer currentOnlines, Integer numberOfVisitorsToday);

}
