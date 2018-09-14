package io.choerodon.notify.websocket.client;


import io.choerodon.notify.websocket.RelationshipDefiningInter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketSession;

@Component
public class SubReceiveMsgHandler implements ReceiveMsgHandler<String> {

    private static final String SUB = "sub";

    private RelationshipDefiningInter relationshipDefiningInter;

    public SubReceiveMsgHandler(RelationshipDefiningInter relationshipDefiningInter) {
        this.relationshipDefiningInter = relationshipDefiningInter;
    }

    @Override
    public void handle(WebSocketSession session, String key) {
         if (!StringUtils.isEmpty(key)) {
             relationshipDefiningInter.contactWebSocketSessionWithKey(key, session);
         }
    }

    @Override
    public String matchType() {
        return SUB;
    }

}
