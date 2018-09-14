package io.choerodon.notify.websocket;

import io.choerodon.notify.websocket.ws.MessageOperatorBuilder;
import io.choerodon.notify.websocket.ws.WebSocketPayload;
import org.springframework.web.socket.WebSocketSession;

public interface MessageSender {

    MessageOperatorBuilder dsl();

    void sendWebSocket(WebSocketSession session, WebSocketPayload<?> payload);

    void sendRedis(String channel, WebSocketPayload<?> payload);

    void sendRedisDefaultChannel(WebSocketPayload<?> payload);

}
