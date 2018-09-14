package io.choerodon.notify.websocket.path;

import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

public abstract class PathMatchHandler {

    private final AntPathMatcher matcher = new AntPathMatcher();

    public abstract String matchPath();

    public abstract String generateKey(Map<String, String> pathKeyValue);

    public abstract void pathHandler(WebSocketSession session, String key);

    public void sessionHandlerAfterConnected(WebSocketSession session){
        String path = matchPath();
        Map<String, String> map = matcher.extractUriTemplateVariables(path, session.getUri().getPath());
        String key = generateKey(map);
        if (key != null) {
            pathHandler(session, key);
        }
    }

}
