package io.choerodon.notify.websocket.notify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketSession;

import io.choerodon.notify.websocket.receive.ReceiveMsgHandler;
import io.choerodon.notify.websocket.relationship.RelationshipDefining;
import io.choerodon.notify.websocket.send.MessageSender;
import io.choerodon.notify.websocket.send.WebSocketSendPayload;

@Component
public class UnSubReceiveMessageHandler implements ReceiveMsgHandler<String> {
    private static final String UNSUB = "unsub";
    private static final Logger LOGGER = LoggerFactory.getLogger(UnSubReceiveMessageHandler.class);

    private RelationshipDefining relationshipDefining;

    private MessageSender messageSender;

    public UnSubReceiveMessageHandler(RelationshipDefining relationshipDefining,
                                      MessageSender messageSender) {
        this.relationshipDefining = relationshipDefining;
        this.messageSender = messageSender;
    }

    @Override
    public String matchType() {
        return UNSUB;
    }

    @Override
    public void handle(WebSocketSession session, String key) {
        LOGGER.info("webSocket unsub {},session id ={}", key, session.getId());
        if (!StringUtils.isEmpty(key)) {
            relationshipDefining.removeKeyContact(session, key);
            messageSender.sendWebSocket(session, new WebSocketSendPayload<>(UNSUB, null, relationshipDefining.getKeysBySession(session)));
        }
    }

}
