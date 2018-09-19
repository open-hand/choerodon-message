package io.choerodon.notify.websocket;

import io.choerodon.notify.websocket.notify.ReceiveRedisMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisAutoConfigure {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisAutoConfigure.class);

    private RedisRegister redisRegister;

    public RedisAutoConfigure(RedisRegister redisRegister) {
        this.redisRegister = redisRegister;
    }

    @Bean
    MessageListenerAdapter defaultListenerAdapter(ReceiveRedisMessageListener receiveRedisMessageListener) {
        return new MessageListenerAdapter(receiveRedisMessageListener, "receiveMessage");
    }

    @Bean
    RedisMessageListenerContainer defaultContainer(RedisConnectionFactory connectionFactory,
                                                   MessageListenerAdapter messageListenerAdapter) {
        PatternTopic topic = new PatternTopic(redisRegister.channelName());
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(messageListenerAdapter, topic);
        LOGGER.info("Begin listen redis channel: {}", topic);
        return container;
    }
}
