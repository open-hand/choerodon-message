package io.choerodon.notify.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.notify.websocket.ws.WebSocketPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Component
public class DefaultSmartMessageSendOperator implements MessageSendOperator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSendOperator.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String defaultChannel;

    public DefaultSmartMessageSendOperator(@Qualifier("defaultChannel") String defaultChannel) {
        this.defaultChannel = defaultChannel;
    }

    @Override
    public void sendToWebSocket(final WebSocketSession webSocketSession, final WebSocketPayload<?> webSocketPayload) {
        try {
            if (webSocketSession.isOpen()) {
                webSocketSession.sendMessage(new TextMessage(convertPayloadToJson(webSocketPayload)));
            }
        } catch (IOException e) {
            LOGGER.warn("error.messageSendOperator.IOException", e);
        }
    }

    @Override
    public void sendToAllWebSockets(final WebSocketPayload<?> webSocketPayload) {

    }

    @Override
    public void sendToRedis(final String channel, final WebSocketPayload<?> webSocketPayload) {

    }

    @Override
    public void smartSend(final WebSocketPayload<?> webSocketPayload) {

    }

    private byte[] convertPayloadToJson(final WebSocketPayload<?> webSocketPayload) throws IOException {
        return objectMapper.writeValueAsBytes(webSocketPayload);
    }

}
