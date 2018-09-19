package io.choerodon.notify.websocket.notify;


import io.choerodon.notify.websocket.send.MessageSender;
import io.choerodon.notify.websocket.receive.ReceiveMsgHandler;
import io.choerodon.notify.websocket.relationship.RelationshipDefining;
import io.choerodon.notify.websocket.send.WebSocketSendPayload;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketSession;

@Component
public class SubReceiveMsgHandler implements ReceiveMsgHandler<String> {

    private static final String SUB = "sub";

    private RelationshipDefining relationshipDefining;

    private MessageSender messageSender;

    public SubReceiveMsgHandler(RelationshipDefining relationshipDefining,
                                MessageSender messageSender) {
        this.relationshipDefining = relationshipDefining;
        this.messageSender = messageSender;
    }

    @Override
    public String matchType() {
        return SUB;
    }

    @Override
    public void handle(WebSocketSession session, String key) {
        if (!StringUtils.isEmpty(key)) {
            relationshipDefining.contact(key, session);
            messageSender.sendWebSocket(session, new WebSocketSendPayload<>(SUB, null, relationshipDefining.getKeysBySession(session)));
        }
    }

}
