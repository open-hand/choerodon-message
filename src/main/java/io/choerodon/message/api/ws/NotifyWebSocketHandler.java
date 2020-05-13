package io.choerodon.message.api.ws;

import org.hzero.websocket.handler.SocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.*;

import io.choerodon.message.infra.utils.OnlineCountStorageUtils;

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

    private OnlineCountStorageUtils onlineCountStorageUtils;

    public NotifyWebSocketHandler(OnlineCountStorageUtils onlineCountStorageUtils) {
        this.onlineCountStorageUtils = onlineCountStorageUtils;
    }

    @Override
    public String processor() {
        return "choerodon_msg";
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String userId = session.getHandshakeHeaders().getFirst("X-WebSocket-UserID");
        //今日访问人数+1，在线人数+1,发送在线信息
        LOGGER.info("Get sitMsg's subscription,sessionId: {}", session.getId());
        Integer originCount = onlineCountStorageUtils.getOnlineCount();
        onlineCountStorageUtils.addNumberOfVisitorsToday(userId);
        onlineCountStorageUtils.addOnlineCount(userId, session.getId());
        if (!originCount.equals(onlineCountStorageUtils.getOnlineCount())) {
            onlineCountStorageUtils.makeVisitorsInfo();
            // TODO 发送在线消息          有没有必要
//            webSocketHelper.sendMessageByKey(ONLINE_INFO_KEY_PATH, new SendMessagePayload<>(NotifyReceiveMessageHandler.ONLINE_INFO_CODE, NotifyReceiveMessageHandler.ONLINE_INFO_KEY_PATH, onlineCountStorageUtils.makeVisitorsInfo()));
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {

    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        LOGGER.info("Notify Socket handler: received message: {}", message.getPayload());
//        if (!StringUtils.isEmpty(key)) {
//            webSocketHelper.subscribe(key, session);
//            webSocketHelper.sendMessageBySession(session, new SendMessagePayload<>("contact", key, "ok"));
//        }
//        if (matcher.match(SIT_MSG_KEY_PATH, key)) {
//            // TODO 这里应该是前端定时给后端发送消息以获取未读消息数的逻辑 (为什么不用后端主动推送？)
//            Map<String, String> map = matcher.extractUriTemplateVariables(SIT_MSG_KEY_PATH, key);
//            String code = map.get("code");
//            if (SITE_MSG_CODE.equals(code)) {
//                String id = map.get("id");
//                if (id != null) {
//                    int unReadNum = siteMsgRecordMapper.selectCountOfUnRead(Long.parseLong(id));
//                    if (unReadNum > 0) {
//                        webSocketHelper.sendMessageBySession(session, new SendMessagePayload<>(MSG_TYPE_PM, key, unReadNum));
//                    }
//                }
//            }
//        } else if (matcher.match(ONLINE_INFO_KEY_PATH, key)) {
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
