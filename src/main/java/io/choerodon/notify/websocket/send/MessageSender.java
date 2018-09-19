package io.choerodon.notify.websocket.send;

import org.springframework.web.socket.WebSocketSession;

public interface MessageSender {

    void sendWebSocket(WebSocketSession session, WebSocketSendPayload<?> payload);

    void sendWebSocket(WebSocketSession session, String json);

    void sendWebSocketByKey(String key, String json);

    void sendRedis(String channel, WebSocketSendPayload<?> payload);

    void sendByKey(String key, WebSocketSendPayload<?> payload);

}
