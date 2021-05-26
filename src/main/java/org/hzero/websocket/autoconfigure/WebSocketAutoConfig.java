package org.hzero.websocket.autoconfigure;

import org.hzero.websocket.broadcast.BroadcastProcessor;
import org.hzero.websocket.broadcast.WebsocketStreamDefinition;
import org.hzero.websocket.config.WebSocketConfig;
import org.hzero.websocket.constant.WebSocketConstant;
import org.hzero.websocket.handler.WebSocketHandler;
import org.hzero.websocket.interceptor.WebSocketInterceptor;
import org.hzero.websocket.registry.BaseSessionRegistry;
import org.hzero.websocket.registry.TopicRegistry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.stream.binding.BindingBeanDefinitionRegistryUtils;
import org.springframework.cloud.stream.binding.MyStreamListenerAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.ClassUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * 包扫描
 *
 * @author shuangfei.zhu@hand-china.com 2018/11/13 13:31
 */
@Configuration
@EnableWebSocket
@ComponentScan(basePackages = "org.hzero.websocket")
public class WebSocketAutoConfig implements WebSocketConfigurer {

    private final WebSocketConfig config;

    public WebSocketAutoConfig(WebSocketConfig config, DefaultListableBeanFactory registry) {
        this.config = config;

        this.initStream(config, registry);
    }

    private void initStream(WebSocketConfig config, DefaultListableBeanFactory registry) {
        if (!WebSocketConstant.ChannelType.STREAM.equals(config.getBroadcast().getChannelType())) {
            return;
        }
        // 代替@EnableBinding(WebsocketStreamDefinition.class)， 使用配置加载通道绑定
        Class<?> type = WebsocketStreamDefinition.class;
        if (!registry.containsBeanDefinition(type.getName())) {
            BindingBeanDefinitionRegistryUtils.registerBindingTargetBeanDefinitions(type, type.getName(), registry);
            BindingBeanDefinitionRegistryUtils.registerBindingTargetsQualifiedBeanDefinitions(ClassUtils.resolveClassName(WebSocketAutoConfig.class.getName(), null), type, registry);
        }
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 简易模式下不开放端口
        if (config.isEasyMode()) {
            return;
        }
        // webSocket通道
        registry.addHandler(new WebSocketHandler(), config.getWebsocket())
                .addInterceptors(new WebSocketInterceptor())
                .setAllowedOrigins("*");
        // sockJs通道
        registry.addHandler(new WebSocketHandler(), config.getSockJs())
                .addInterceptors(new WebSocketInterceptor())
                .setAllowedOrigins("*")
                .withSockJS();
    }

    /**
     * 根据配置注入。不注入的话，@MyStreamListener注解就不会绑定消息队列的监听
     */
    @Bean
    @ConditionalOnProperty(prefix = WebSocketConfig.PREFIX + ".broadcast", name = "channel-type", havingValue = WebSocketConstant.ChannelType.STREAM)
    MyStreamListenerAnnotationBeanPostProcessor myStreamListenerAnnotationBeanPostProcessor() {
        return new MyStreamListenerAnnotationBeanPostProcessor();
    }

    /**
     * 消息监听器适配器，绑定消息处理器
     */
    @Bean("ws-adapter")
    MessageListenerAdapter listenerAdapter(BroadcastProcessor listener) {
        return new MessageListenerAdapter(listener, "processRedisMessage");
    }

    /**
     * redis消息监听器容器
     */
    @Bean("ws-container")
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory, @Qualifier("ws-adapter") MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        // 订阅通道
        PatternTopic commonTopic = new PatternTopic(WebSocketConstant.CHANNEL);
        PatternTopic brokerTopic = new PatternTopic(BaseSessionRegistry.getBrokerId());
        container.addMessageListener(listenerAdapter, commonTopic);
        container.addMessageListener(listenerAdapter, brokerTopic);
        // 记录topic
        TopicRegistry.addTopic(WebSocketConstant.CHANNEL, commonTopic);
        TopicRegistry.addTopic(BaseSessionRegistry.getBrokerId(), brokerTopic);
        return container;
    }

    @Bean("websocket-check-executor")
    public AsyncTaskExecutor asyncTaskExecutor(WebSocketConfig config) {
        WebSocketConfig.ThreadPoolProperties threadPoolProperties = config.getThreadPoolProperties();
        if (threadPoolProperties == null) {
            threadPoolProperties = new WebSocketConfig.ThreadPoolProperties();
        }
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadPoolProperties.getCorePoolSize());
        executor.setMaxPoolSize(threadPoolProperties.getMaxPoolSize());
        executor.setKeepAliveSeconds(threadPoolProperties.getKeepAliveSeconds());
        executor.setQueueCapacity(threadPoolProperties.getQueueCapacity());
        executor.setAllowCoreThreadTimeOut(threadPoolProperties.isAllowCoreThreadTimeOut());
        executor.setThreadNamePrefix(threadPoolProperties.getThreadNamePrefix());
        return executor;
    }
}