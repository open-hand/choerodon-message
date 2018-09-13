package io.choerodon.notify.websocket.ws;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
public class DefaultClientSubMsgInterceptor implements ClientMsgInterceptor {

    @Override
    public int order() {
        return 0;
    }

    @Override
    public void handleMsg(WebSocketSession session, BinaryMessage message) {

    }

}
