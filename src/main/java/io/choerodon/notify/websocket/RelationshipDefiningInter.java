package io.choerodon.notify.websocket;

import org.springframework.web.socket.WebSocketSession;

import java.util.Set;

public interface RelationshipDefiningInter {

    Set<WebSocketSession> getWebSocketSessionsByKey(String key);

    Set<String> getRedisChannelsByKey(String key);

    void contactWebSocketSessionWithKey(String key, WebSocketSession session);

    void removeWebSocketSessionRelationship(WebSocketSession session);

}
