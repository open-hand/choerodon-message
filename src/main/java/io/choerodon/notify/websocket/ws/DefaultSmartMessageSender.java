package io.choerodon.notify.websocket.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.notify.websocket.MessageSender;
import io.choerodon.notify.websocket.RelationshipDefining;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Component
public class DefaultSmartMessageSender implements MessageSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSender.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private StringRedisTemplate redisTemplate;

    private RelationshipDefining relationshipDefining;

    public DefaultSmartMessageSender(StringRedisTemplate redisTemplate,
                                     RelationshipDefining relationshipDefining) {
        this.redisTemplate = redisTemplate;
        this.relationshipDefining = relationshipDefining;
    }

    @Override
    public void sendWebSocket(WebSocketSession session, WebSocketSendPayload<?> payload) {
        if (payload == null) {
            LOGGER.warn("error.messageOperator.sendWebSocket.payloadIsNull");
            return;
        }
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsBytes(payload)));
            } catch (IOException e) {
                LOGGER.warn("error.messageOperator.sendWebSocket.IOException, payload: {}", payload, e);
            }
        }
    }

    @Override
    public void sendRedis(String channel, WebSocketSendPayload<?> payload) {
        if (payload == null) {
            LOGGER.warn("error.messageOperator.sendRedis.payloadIsNull");
            return;
        }
        try {
            redisTemplate.convertAndSend(channel, objectMapper.writeValueAsString(payload));
        } catch (JsonProcessingException e) {
            LOGGER.warn("error.messageOperator.sendRedisDefaultChannel.JsonProcessingException, payload: {}", payload, e);
        }
    }

    @Override
    public void sendWebSocket(WebSocketSession session, String json) {
        if (session != null && json != null) {
            try {
                session.sendMessage(new TextMessage(json));
            } catch (IOException e) {
                LOGGER.warn("error.messageOperator.sendWebSocket.IOException, json: {}", json, e);
            }
        }
    }

    @Override
    public void sendRedis(String channel, String json) {
        if (!StringUtils.isEmpty(channel) && json != null) {
            redisTemplate.convertAndSend(channel, json);
        }
    }

    @Override
    public void sendByKey(String key, WebSocketSendPayload<?> payload) {
        if (!StringUtils.isEmpty(key) && payload != null) {
            relationshipDefining.getWebSocketSessionsByKey(key).forEach(session -> this.sendWebSocket(session, payload));
            relationshipDefining.getRedisChannelsByKey(key, true).forEach(redis -> this.sendRedis(redis, payload));
        }

    }

    @Override
    public void sendByKey(String key, String json) {
        if (!StringUtils.isEmpty(key) && json != null) {
            relationshipDefining.getWebSocketSessionsByKey(key).forEach(session -> this.sendWebSocket(session, json));
            relationshipDefining.getRedisChannelsByKey(key, true).forEach(redis -> this.sendRedis(redis, json));
        }
    }
}

