package io.choerodon.notify.api.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.notify.api.dto.UserDTO;
import io.choerodon.notify.infra.dto.SendSettingDTO;
import io.choerodon.notify.infra.dto.WebHookDTO;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.Set;

public interface WebSocketSendService {

    void sendSiteMessage(String code, Map<String, Object> params, Set<UserDTO> targetUsers, Long sendBy, String senderType, SendSettingDTO sendSetting);

    void sendWebSocket(String code, String id, String message);
}