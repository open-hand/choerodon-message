package io.choerodon.notify.websocket.ws;

import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

public interface ClientMsgInterceptor {

    int order();

    void handleMsg(WebSocketSession session, BinaryMessage message);

}
