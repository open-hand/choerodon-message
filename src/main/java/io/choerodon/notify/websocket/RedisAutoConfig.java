package io.choerodon.notify.websocket;

import io.choerodon.notify.websocket.redis.DefaultRedisChannelListener;
import io.choerodon.notify.websocket.redis.RedisOperatorHelper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
public class RedisAutoConfig {

    private Environment environment;

    public RedisAutoConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean(name = "defaultChannel")
    String defaultChannel() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress() + ":" + environment.getProperty("server.port");
    }

    @Bean
    RedisMessageListenerContainer defaultContainer(RedisConnectionFactory connectionFactory,
                                                   MessageListenerAdapter listenerAdapter,
                                                   @Qualifier("defaultChannel") String defaultChannel) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        container.addMessageListener(listenerAdapter, new PatternTopic(defaultChannel));
        return container;
    }

    @Bean
    MessageListenerAdapter defaultListenerAdapter(DefaultRedisChannelListener defaultRedisChannelListener) {
        return new MessageListenerAdapter(defaultRedisChannelListener, "receiveMessage");
    }

    @Bean
    RedisOperatorHelper redisOperatorHelper(RedisTemplate<Object, Object> redisTemplate) {
        return new RedisOperatorHelper(redisTemplate);
    }

}
