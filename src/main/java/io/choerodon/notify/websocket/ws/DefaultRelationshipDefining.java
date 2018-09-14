package io.choerodon.notify.websocket.ws;

import io.choerodon.notify.websocket.RelationshipDefiningInter;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultRelationshipDefining implements RelationshipDefiningInter {

    private Map<String, Set<WebSocketSession>> keySessionMap = new ConcurrentHashMap<>();

    @Override
    public Set<WebSocketSession> getWebSocketSessionsByKey(String key) {
        return keySessionMap.getOrDefault(key, Collections.emptySet());
    }

    @Override
    public Set<String> getRedisChannelsByKey(String key) {
        return null;
    }

    @Override
    public void contactWebSocketSessionWithKey(String key, WebSocketSession session) {
        if (!StringUtils.isEmpty(key) && session != null) {
            Set<WebSocketSession> sessions = keySessionMap.computeIfAbsent(key, k -> new HashSet<>());
            sessions.add(session);
        }
    }

    @Override
    public void removeWebSocketSessionRelationship(WebSocketSession delSession) {
        if (delSession == null) {
            return;
        }
        Iterator<Map.Entry<String, Set<WebSocketSession>>> it = keySessionMap.entrySet().iterator();
        while (it.hasNext()) {
            Set<WebSocketSession> sessions = it.next().getValue();
            sessions.removeIf(t -> t.equals(delSession));
            if (sessions.isEmpty()) {
                it.remove();
            }
        }
    }
}
