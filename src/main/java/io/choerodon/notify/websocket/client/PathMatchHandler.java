package io.choerodon.notify.websocket.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

public abstract class PathMatchHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(PathMatchHandler.class);

    private final AntPathMatcher matcher = new AntPathMatcher();

    public abstract String matchPath();

    public abstract String generateKey(Map<String, String> pathKeyValue);

    public abstract void pathHandler(WebSocketSession session, String key);

    public void sessionHandlerAfterConnected(WebSocketSession session) {
        String path = matchPath();
        String uri = session.getUri().getPath();
        if (StringUtils.isEmpty(path)) {
            LOGGER.error("pathMatchHandler's matchPath cannot be null, pathMatchHandler {}", this.getClass().getName());
            return;
        }
        if (matcher.match(path, uri)) {
            Map<String, String> map = matcher.extractUriTemplateVariables(path, uri);
            String key = generateKey(map);
            if (key != null) {
                pathHandler(session, key);
                LOGGER.info("websocket uri {}, subscribe key is {}, use pathMatchHandler {}", uri, key, this.getClass().getName());
            }
        } else {
            LOGGER.debug("websocket uri {}, mismatch pathMatchHandler {}", uri, this.getClass().getName());
        }
    }

}
