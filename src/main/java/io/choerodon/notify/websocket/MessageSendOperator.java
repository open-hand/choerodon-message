package io.choerodon.notify.websocket;

import io.choerodon.notify.websocket.ws.WebSocketPayload;
import org.springframework.web.socket.WebSocketSession;

public interface MessageSendOperator {

    void sendToWebSocket(final WebSocketSession webSocketSession, WebSocketPayload<?> webSocketPayload);

    void sendToAllWebSockets(final WebSocketPayload<?> webSocketPayload);

    void sendToRedis(final String channel, final WebSocketPayload<?> webSocketPayload);

    void smartSend(final WebSocketPayload<?> webSocketPayload);

}
