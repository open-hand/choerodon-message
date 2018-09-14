package io.choerodon.notify.websocket.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.notify.websocket.MessageSender;
import io.choerodon.notify.websocket.RelationshipDefiningInter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Component
public class DefaultSmartMessageSender implements MessageSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSender.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private StringRedisTemplate redisTemplate;

    private String defaultChannel;

    private RelationshipDefiningInter relationshipDefiningInter;

    public DefaultSmartMessageSender(@Qualifier("defaultChannel") String defaultChannel,
                                     StringRedisTemplate redisTemplate,
                                     RelationshipDefiningInter relationshipDefiningInter) {
        this.defaultChannel = defaultChannel;
        this.redisTemplate = redisTemplate;
    }


    @Override
    public MessageOperatorBuilder dsl() {
        return new MessageOperatorBuilder(this, relationshipDefiningInter);
    }


    @Override
    public void sendWebSocket(WebSocketSession session, WebSocketPayload<?> payload) {
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
    public void sendRedis(String channel, WebSocketPayload<?> payload) {
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
    public void sendRedisDefaultChannel(WebSocketPayload<?> payload) {
        if (payload == null) {
            LOGGER.warn("error.messageOperator.sendRedisDefaultChannel.payloadIsNull");
            return;
        }
        try {
            redisTemplate.convertAndSend(defaultChannel, objectMapper.writeValueAsString(payload));
        } catch (JsonProcessingException e) {
            LOGGER.warn("error.messageOperator.sendRedisDefaultChannel.JsonProcessingException, payload: {}", payload, e);
        }
    }

}

