package io.choerodon.notify.websocket;

import io.choerodon.notify.websocket.handshake.AuthHandshakeInterceptor;
import io.choerodon.notify.websocket.handshake.WebSocketHandshakeInterceptor;
import io.choerodon.notify.websocket.ws.DefaultRelationshipDefining;
import io.choerodon.notify.websocket.ws.WebSocketMessageHandler;
import io.choerodon.notify.websocket.ws.WebSocketProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
@EnableWebSocket
@EnableConfigurationProperties(WebSocketProperties.class)
public class WebSocketEndpointConfigure implements WebSocketConfigurer {

    @Autowired
    private WebSocketMessageHandler webSocketHandler;

    @Autowired
    private WebSocketProperties webSocketProperties;

    @Autowired
    private Optional<List<WebSocketHandshakeInterceptor>> handshakeInterceptors;

    @Bean
    @ConditionalOnProperty(prefix = "choerodon.ws", name = "oauth")
    AuthHandshakeInterceptor authHandshakeInterceptor() {
        return new AuthHandshakeInterceptor(webSocketProperties);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        List<WebSocketHandshakeInterceptor> interceptors = handshakeInterceptors.orElseGet(Collections::emptyList);
        WebSocketHandshakeInterceptor[] webSocketHandshakeInterceptors = new WebSocketHandshakeInterceptor[interceptors.size()];
        interceptors.toArray(webSocketHandshakeInterceptors);
        registry.addHandler(webSocketHandler, webSocketProperties.getPaths())
                .setAllowedOrigins("*")
                .addInterceptors(webSocketHandshakeInterceptors);

    }

    @Bean
    @ConditionalOnMissingBean
    public RelationshipDefining relationshipDefiningInter(StringRedisTemplate redisTemplate, RedisRegister redisRegister) {
        return new DefaultRelationshipDefining(redisTemplate, redisRegister);
    }

    @Bean(name = "registerHeartBeat")
    public ScheduledExecutorService registerHeartBeat() {
        return Executors.newScheduledThreadPool(1);
    }
}
