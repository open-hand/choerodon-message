package io.choerodon.notify.websocket.ws;

import java.util.UUID;

public class WebSocketUtils {

    private WebSocketUtils() {
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

}
