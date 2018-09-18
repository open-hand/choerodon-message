package io.choerodon.notify.websocket.notify;

import io.choerodon.notify.infra.mapper.SiteMsgRecordMapper;
import io.choerodon.notify.websocket.MessageSender;
import io.choerodon.notify.websocket.RelationshipDefining;
import io.choerodon.notify.websocket.client.PathMatchHandler;
import io.choerodon.notify.websocket.ws.WebSocketSendPayload;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

import static io.choerodon.notify.api.service.impl.WebSocketWsSendServiceImpl.MSG_TYPE_PM;

@Component
public class SiteMsgPathMatchHandler extends PathMatchHandler {

    private RelationshipDefining relationshipDefining;

    private final SiteMsgRecordMapper siteMsgRecordMapper;

    private final MessageSender messageSender;

    public SiteMsgPathMatchHandler(RelationshipDefining relationshipDefining,
                                   SiteMsgRecordMapper siteMsgRecordMapper,
                                   MessageSender messageSender) {
        this.relationshipDefining = relationshipDefining;
        this.siteMsgRecordMapper = siteMsgRecordMapper;
        this.messageSender = messageSender;
    }

    @Override
    public String matchPath() {
        return "/choerodon:msg/{code}/{id}";
    }

    @Override
    public String generateKey(Map<String, String> pathKeyValue) {
        String code = pathKeyValue.get("code");
        String id = pathKeyValue.get("id");
        if (StringUtils.isEmpty(code) || StringUtils.isEmpty(id)) {
            return null;
        }
        return "choerodon:msg:" + code + ":" + id;
    }

    @Override
    public void pathHandler(WebSocketSession session, String key, Map<String, String> pathKeyValue) {
        String id = pathKeyValue.get("id");
        relationshipDefining.contact(key, session);
        messageSender.sendWebSocket(session, new WebSocketSendPayload<>(MSG_TYPE_PM, key, siteMsgRecordMapper.selectCountOfUnRead(Long.parseLong(id))));
    }


}
