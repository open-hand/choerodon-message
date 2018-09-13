package io.choerodon.notify.websocket.ws;

import io.choerodon.notify.websocket.MessageSendOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;

import static io.choerodon.notify.websocket.ws.WebSocketPayload.MSG_TYPE_SESSION;

@Component
public class WebSocketMessageHandler extends TextWebSocketHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketMessageHandler.class);

    private MessageSendOperator messageSendOperator;

    private List<ClientMsgInterceptor> clientMsgInterceptors;

    public WebSocketMessageHandler(List<ClientMsgInterceptor> clientMsgInterceptors, MessageSendOperator messageSendOperator) {
        this.clientMsgInterceptors = clientMsgInterceptors;
        this.messageSendOperator = messageSendOperator;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        LOGGER.info("new websocket connect session {}, uri {}", session.getId(), session.getUri());
        messageSendOperator.sendToWebSocket(session, new WebSocketPayload<>(MSG_TYPE_SESSION, session.getId()));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);

    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        super.handleBinaryMessage(session, message);
    }

}
