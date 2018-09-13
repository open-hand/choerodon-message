package io.choerodon.notify.websocket;

import io.choerodon.notify.websocket.ws.AbstractHandshakeInterceptor;
import io.choerodon.notify.websocket.ws.WebSocketMessageHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.List;

@Configuration
@EnableWebSocket
@EnableConfigurationProperties(WebSocketProperties.class)
public class WebSocketEndpointConfig implements WebSocketConfigurer {

    private WebSocketMessageHandler webSocketHandler;

    private WebSocketProperties webSocketProperties;

    private List<AbstractHandshakeInterceptor> handshakeInterceptors;

    public WebSocketEndpointConfig(WebSocketMessageHandler webSocketHandler,
                                   WebSocketProperties webSocketProperties,
                                   List<AbstractHandshakeInterceptor> handshakeInterceptors) {
        this.webSocketHandler = webSocketHandler;
        this.webSocketProperties = webSocketProperties;
        this.handshakeInterceptors = handshakeInterceptors;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        AbstractHandshakeInterceptor[] abstractHandshakeInterceptors = new AbstractHandshakeInterceptor[handshakeInterceptors.size()];
        handshakeInterceptors.toArray(abstractHandshakeInterceptors);
        registry.addHandler(webSocketHandler, webSocketProperties.getPaths())
                .setAllowedOrigins("*")
                .addInterceptors(abstractHandshakeInterceptors);

    }
}
