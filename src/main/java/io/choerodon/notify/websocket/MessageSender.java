package io.choerodon.notify.websocket;

import io.choerodon.notify.websocket.ws.WebSocketSendPayload;
import org.springframework.web.socket.WebSocketSession;

public interface MessageSender {

    void sendWebSocket(WebSocketSession session, WebSocketSendPayload<?> payload);

    void sendWebSocket(WebSocketSession session, String json);

    void sendRedis(String channel, WebSocketSendPayload<?> payload);

    void sendRedis(String channel, String json);

    void sendByKey(String key, WebSocketSendPayload<?> payload);

    void sendByKey(String key, String json);
}
