package io.choerodon.notify.websocket.ws;

import io.choerodon.notify.websocket.MessageSender;
import io.choerodon.notify.websocket.RelationshipDefining;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MessageOperatorBuilder {

    private Match match = new Match();

    private MessageSender operator;

    private WebSocketPayload<?> payload;

    private RelationshipDefining relationshipDefining;

    public MessageOperatorBuilder(MessageSender operator,
                                  RelationshipDefining relationshipDefining) {
        this.operator = operator;
        this.relationshipDefining = relationshipDefining;
    }

    public MessageOperatorBuilder where(Key key) {
        match.keys.addAll(key.getKeys());
        return this;
    }

    public MessageOperatorBuilder where(Session session) {
        match.sessions.addAll(session.getSessions());
        return this;
    }

    public MessageOperatorBuilder where(RedisChannel redisChannel) {
        match.isDefaultChannel = redisChannel.isDefaultChannel();
        if (!match.isDefaultChannel) {
            match.redisChannels.addAll(redisChannel.getChannels());
        }
        return this;
    }

    public MessageOperatorBuilder payload(WebSocketPayload<?> payload) {
        this.payload = payload;
        return this;
    }

    public void sendAll() {
        sendByKey();
        sendRedis();
        sendWebSocket();
    }

    public void sendByKey() {
        match.keys.forEach(k -> {
            relationshipDefining.getWebSocketSessionsByKey(k).forEach(session -> operator.sendWebSocket(session, payload));
            relationshipDefining.getRedisChannelsByKey(k).forEach(redis -> operator.sendRedis(redis, payload));
        });
    }

    public void sendRedis() {
        if (match.isDefaultChannel) {
            operator.sendRedisDefaultChannel(payload);
        } else {
            match.redisChannels.forEach(t -> operator.sendRedis(t, payload));
        }
    }

    public void sendWebSocket() {
        match.sessions.forEach(t -> operator.sendWebSocket(t, payload));
    }


    public static class Session {
        private List<WebSocketSession> sessions;

        private Session(List<WebSocketSession> sessions) {
            this.sessions = sessions;
        }

        public static Session eq(WebSocketSession session) {
            return new Session(Collections.singletonList(session));
        }

        public static Session in(WebSocketSession... sessions) {
            return new Session(Arrays.asList(sessions));
        }

        List<WebSocketSession> getSessions() {
            return sessions;
        }
    }

    public static class Key {

        private List<String> keys;

        private Key(List<String> keys) {
            this.keys = keys;
        }

        public static Key eq(String key) {
            return new Key(Collections.singletonList(key));
        }

        public static Key in(String... keys) {
            return new Key(Arrays.asList(keys));
        }

        List<String> getKeys() {
            return keys;
        }
    }

    class RedisChannel {

        private List<String> channels;
        private boolean isDefaultChannel;

        private RedisChannel(List<String> channels, boolean isDefaultChannel) {
            this.channels = channels;
        }

        public RedisChannel eq(String channel) {
            return new RedisChannel(Collections.singletonList(channel), false);
        }

        public RedisChannel in(String... channels) {
            return new RedisChannel(Arrays.asList(channels), false);
        }

        public RedisChannel defaultChannel() {
            return new RedisChannel(Collections.emptyList(), true);
        }

        List<String> getChannels() {
            return channels;
        }

        public boolean isDefaultChannel() {
            return isDefaultChannel;
        }
    }

    class Match {
        List<String> keys;
        List<WebSocketSession> sessions;
        List<String> redisChannels;
        boolean isDefaultChannel;

        Match() {
            this.keys = new ArrayList<>();
            this.sessions = new ArrayList<>();
            this.redisChannels = new ArrayList<>();
        }
    }
}








