package io.choerodon.notify.websocket.ws;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.notify.websocket.MessageSender;
import io.choerodon.notify.websocket.RelationshipDefining;
import io.choerodon.notify.websocket.client.PathMatchHandler;
import io.choerodon.notify.websocket.client.ReceiveMsgHandler;
import io.choerodon.notify.websocket.exception.MsgHandlerDuplicateMathTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;

import static io.choerodon.notify.websocket.ws.WebSocketSendPayload.MSG_TYPE_SESSION;

@Component
public class WebSocketMessageHandler extends TextWebSocketHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketMessageHandler.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MessageSender messageSender;

    private List<PathMatchHandler> pathMatchHandlers;

    private RelationshipDefining relationshipDefining;

    private final Map<String, HandlerInfo> typeClassMap = new HashMap<>(2 << 4);

    public WebSocketMessageHandler(Optional<List<ReceiveMsgHandler>> msgHandlers,
                                   Optional<List<PathMatchHandler>> optionalMatchHandlers,
                                   RelationshipDefining relationshipDefining,
                                   MessageSender messageSender) {
        msgHandlers.orElseGet(Collections::emptyList).forEach(t -> {
            if (typeClassMap.get(t.matchType()) == null) {
                typeClassMap.put(t.matchType(), new HandlerInfo(t.payloadClass(), t));
            } else {
                throw new MsgHandlerDuplicateMathTypeException(t);
            }
        });
        this.messageSender = messageSender;
        this.pathMatchHandlers = optionalMatchHandlers.orElseGet(Collections::emptyList);
        this.relationshipDefining = relationshipDefining;
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        LOGGER.info("new websocket connect session {}, uri {}", session.getId(), session.getUri());
        pathMatchHandlers.forEach(handler -> handler.sessionHandlerAfterConnected(session));
        messageSender.sendWebSocket(session, new WebSocketSendPayload<>(MSG_TYPE_SESSION, null, session.getId()));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        this.relationshipDefining.removeWebSocketSessionContact(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
        LOGGER.error("error.webSocketMessageHandler.handleTransportError", exception);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        super.handleBinaryMessage(session, message);
        String receiveMsg = new String(message.getPayload().array());
        try {
            JSONObject jsonObject = new JSONObject(receiveMsg);
            String type = jsonObject.getString("type");
            if (type != null) {
                HandlerInfo handlerInfo = typeClassMap.get(type);
                if (handlerInfo != null) {
                    JavaType javaType = objectMapper.getTypeFactory().constructParametricType(WebSocketReceivePayload.class, handlerInfo.getClass());
                    WebSocketReceivePayload<?> payload = objectMapper.readValue(receiveMsg, javaType);
                    handlerInfo.msgHandler.handle(session, payload);
                } else {
                    LOGGER.warn("abandon message that can not find msgHandler, message {}", receiveMsg);
                }
            } else {
                LOGGER.warn("abandon message that does't have 'type' field, message {}", receiveMsg);
            }
        } catch (Exception e) {
            LOGGER.warn("abandon message received from client, msgHandler error, message: {}", receiveMsg, e);
        }
    }

    final class HandlerInfo {
        final Class<?> payloadType;
        final ReceiveMsgHandler msgHandler;

        HandlerInfo(Class<?> payloadType, ReceiveMsgHandler msgHandler) {
            this.payloadType = payloadType;
            this.msgHandler = msgHandler;
        }
    }

}
