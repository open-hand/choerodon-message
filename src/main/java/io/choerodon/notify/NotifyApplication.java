package io.choerodon.notify;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import io.choerodon.eureka.event.EurekaEventHandler;
import io.choerodon.resource.annoation.EnableChoerodonResourceServer;

@SpringBootApplication
@EnableFeignClients("io.choerodon")
@EnableEurekaClient
@EnableChoerodonResourceServer
@EnableAsync
public class NotifyApplication {

    public static void main(String[] args) {
        EurekaEventHandler.getInstance().init();
        SpringApplication.run(NotifyApplication.class, args);
    }

    @Bean
    @Qualifier("notify-executor")
    public AsyncTaskExecutor asyncSendNoticeExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("send-notify-executor");
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(99999);
        return executor;
    }
}

