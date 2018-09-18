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

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class RedisAutoConfigure {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisAutoConfigure.class);

    private RelationshipDefining relationshipDefining;

    public RedisAutoConfigure(RelationshipDefining relationshipDefining) {
        this.relationshipDefining = relationshipDefining;
    }

    @Bean
    MessageListenerAdapter defaultListenerAdapter(ReceiveRedisMessageListener receiveRedisMessageListener) {
        return new MessageListenerAdapter(receiveRedisMessageListener, "receiveMessage");
    }

    @Bean
    RedisMessageListenerContainer defaultContainer(RedisConnectionFactory connectionFactory,
                                                   MessageListenerAdapter messageListenerAdapter) {
        List<PatternTopic> topics = relationshipDefining.selfSubChannels().stream().map(PatternTopic::new).collect(Collectors.toList());
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(messageListenerAdapter, topics);
        LOGGER.info("begin listen redis channels: {}", topics);
        return container;
    }
}
