package io.choerodon.notify.websocket.client;


import io.choerodon.notify.websocket.RelationshipDefining;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketSession;

@Component
public class SubReceiveMsgHandler implements ReceiveMsgHandler<String> {

    private static final String SUB = "sub";

    private RelationshipDefining relationshipDefining;

    public SubReceiveMsgHandler(RelationshipDefining relationshipDefining) {
        this.relationshipDefining = relationshipDefining;
    }

    @Override
    public void handle(WebSocketSession session, String key) {
         if (!StringUtils.isEmpty(key)) {
             relationshipDefining.contact(key, session);
         }
    }

    @Override
    public String matchType() {
        return SUB;
    }

}
