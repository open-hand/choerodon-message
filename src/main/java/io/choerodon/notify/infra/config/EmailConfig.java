package io.choerodon.notify.infra.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableConfigurationProperties(NotifyProperties.class)
public class EmailConfig {

    private NotifyProperties notifyProperties;

    public EmailConfig(NotifyProperties notifyProperties) {
        this.notifyProperties = notifyProperties;
    }

    @Bean(name = "asyncSendNoticeExecutor")
    public Executor asyncSendNoticeExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(notifyProperties.getAsynSendNoticesThreadNum());
        executor.setMaxPoolSize(notifyProperties.getAsynSendNoticesThreadNum());
        executor.setQueueCapacity(99999);
        executor.setThreadNamePrefix("Notify-send-notice-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

}
