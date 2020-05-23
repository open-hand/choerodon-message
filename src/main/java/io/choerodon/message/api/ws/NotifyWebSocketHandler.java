package io.choerodon.message.api.ws;

import org.hzero.websocket.handler.SocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.*;

/**
 * @author zmf
 * @since 20-5-11
 */
@Component
public class NotifyWebSocketHandler implements SocketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotifyWebSocketHandler.class);

    private static final String SIT_MSG_KEY_PATH = "choerodon:msg:{code}:{id}";
    public static final String ONLINE_INFO_KEY_PATH = "choerodon:msg:online-info";

    private static final String SITE_MSG_CODE = "site-msg";
    public static final String ONLINE_INFO_CODE = "online-info";

    private final AntPathMatcher matcher = new AntPathMatcher();

    @Override
    public String processor() {
        return "choerodon_msg";
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        LOGGER.info("Get sitMsg's subscription,sessionId: {}", session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {

    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        // 这里应该是只会接收到心跳
        LOGGER.info("Notify Socket handler: received message: {}", message.getPayload());
        // TODO 这个订阅的逻辑原先是为了配合io.choerodon.notify.api.controller.v1.NoticesSendController#postWebSocket方法的
//        if (!StringUtils.isEmpty(key)) {
//            webSocketHelper.subscribe(key, session);
//            webSocketHelper.sendMessageBySession(session, new SendMessagePayload<>("contact", key, "ok"));
//        }
//        if (matcher.match(ONLINE_INFO_KEY_PATH, key)) {
        // TODO 这个逻辑前端暂时没找到用处，可以考虑删除
//            webSocketHelper.sendMessageBySession(session, new SendMessagePayload<>(ONLINE_INFO_CODE, ONLINE_INFO_KEY_PATH, onlineCountStorageUtils.makeVisitorsInfo()));
//        }
    }

    @Override
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {

    }

    @Override
    public void handlePongMessage(WebSocketSession session, PongMessage message) {

    }
}
