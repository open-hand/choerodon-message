package io.choerodon.notify.websocket.ws;

import io.choerodon.notify.websocket.RelationshipDefining;
import io.choerodon.notify.websocket.exception.GetSelfSubChannelsFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketSession;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultRelationshipDefining implements RelationshipDefining {

    private static final Logger LOGGER = LoggerFactory.getLogger(RelationshipDefining.class);

    private Map<String, Set<WebSocketSession>> keySessionsMap = new ConcurrentHashMap<>();

    private Map<WebSocketSession, Set<String>> sessionKeysMap = new ConcurrentHashMap<>();

    private StringRedisTemplate redisTemplate;

    private Environment environment;

    private Set<String> selfSubChannels = new HashSet<>();

    public DefaultRelationshipDefining(StringRedisTemplate redisTemplate,
                                       Environment environment) {
        this.redisTemplate = redisTemplate;
        this.environment = environment;
    }

    @Override
    public Set<WebSocketSession> getWebSocketSessionsByKey(String key) {
        return keySessionsMap.getOrDefault(key, Collections.emptySet());
    }

    @Override
    public Set<String> getKeysBySession(WebSocketSession session) {
        return sessionKeysMap.getOrDefault(session, Collections.emptySet());
    }

    @Override
    public Set<String> getRedisChannelsByKey(String key, boolean exceptSelf) {
        Set<String> channels = redisTemplate.opsForSet().members(key);
        if (exceptSelf) {
            channels.removeIf(t -> selfSubChannels().contains(t));
        }
        return channels;
    }

    @Override
    public void contact(String key, WebSocketSession session) {
        if (StringUtils.isEmpty(key)) {
            return;
        }
        if (session != null) {
            Set<WebSocketSession> sessions = keySessionsMap.computeIfAbsent(key, k -> new HashSet<>());
            sessions.add(session);
            Set<String> subKeys = sessionKeysMap.computeIfAbsent(session, k -> new HashSet<>());
            subKeys.add(key);
            LOGGER.info("webSocket subscribe sessionId is {}, subKeys is {}", session.getId(), subKeys);
        }
        Set<String> selfChannels = this.selfSubChannels();
        String[] channels = new String[selfChannels.size()];
        selfChannels.toArray(channels);
        redisTemplate.opsForSet().add(key, channels);
    }

    @Override
    public void removeWebSocketSessionContact(WebSocketSession delSession) {
        if (delSession == null) {
            return;
        }
        sessionKeysMap.remove(delSession);
        Iterator<Map.Entry<String, Set<WebSocketSession>>> it = keySessionsMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Set<WebSocketSession>> next = it.next();
            Set<WebSocketSession> sessions = next.getValue();
            sessions.removeIf(t -> t.equals(delSession));
            if (sessions.isEmpty()) {
                it.remove();
                this.selfSubChannels().forEach(t -> redisTemplate.opsForSet().remove(next.getKey(), t));
            }
        }
    }

    @Override
    public Set<String> selfSubChannels() {
        try {
            if (selfSubChannels.isEmpty()) {
                String channel = InetAddress.getLocalHost().getHostAddress() + ":" + environment.getProperty("server.port");
                selfSubChannels.add(channel);
            }
            return selfSubChannels;
        } catch (UnknownHostException e) {
            throw new GetSelfSubChannelsFailedException(e);
        }
    }
}
