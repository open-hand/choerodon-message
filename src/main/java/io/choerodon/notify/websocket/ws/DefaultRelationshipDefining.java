package io.choerodon.notify.websocket.ws;

import io.choerodon.notify.websocket.RelationshipDefining;
import io.choerodon.notify.websocket.exception.GetSelfSubChannelsFailedException;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketSession;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultRelationshipDefining implements RelationshipDefining {

    private Map<String, Set<WebSocketSession>> keySessionMap = new ConcurrentHashMap<>();

    private StringRedisTemplate redisTemplate;

    private Environment environment;

    public DefaultRelationshipDefining(StringRedisTemplate redisTemplate,
                                       Environment environment) {
        this.redisTemplate = redisTemplate;
        this.environment = environment;
    }

    @Override
    public Set<WebSocketSession> getWebSocketSessionsByKey(String key) {
        return keySessionMap.getOrDefault(key, Collections.emptySet());
    }

    @Override
    public Set<String> getRedisChannelsByKey(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    @Override
    public void contact(String key, WebSocketSession session) {
        if (StringUtils.isEmpty(key)) {
            return;
        }
        if (session != null) {
            Set<WebSocketSession> sessions = keySessionMap.computeIfAbsent(key, k -> new HashSet<>());
            sessions.add(session);
        }
        String[] channels = new String[selfSubChannels().size()];
        selfSubChannels().toArray(channels);
        redisTemplate.opsForSet().add(key, channels);
    }

    @Override
    public void removeWebSocketSessionContact(WebSocketSession delSession) {
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

    @Override
    public Set<String> selfSubChannels() {
        try {
            String channel = InetAddress.getLocalHost().getHostAddress() + ":" + environment.getProperty("server.port");
            return Collections.singleton(channel);
        } catch (UnknownHostException e) {
            throw new GetSelfSubChannelsFailedException(e);
        }
    }
}
