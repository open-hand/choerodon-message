package io.choerodon.notify.websocket.notify;

import io.choerodon.notify.websocket.RelationshipDefining;
import io.choerodon.notify.websocket.client.PathMatchHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

@Component
public class SiteMsgPathMatchHandler extends PathMatchHandler {

    private RelationshipDefining relationshipDefining;

    public SiteMsgPathMatchHandler(RelationshipDefining relationshipDefining) {
        this.relationshipDefining = relationshipDefining;
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
    public void pathHandler(WebSocketSession session, String key) {
        relationshipDefining.contact(key, session);
    }


}
