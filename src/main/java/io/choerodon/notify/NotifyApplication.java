package io.choerodon.notify;

import io.choerodon.eureka.event.EurekaEventHandler;
import io.choerodon.resource.annoation.EnableChoerodonResourceServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients("io.choerodon")
@EnableEurekaClient
@EnableChoerodonResourceServer
public class NotifyApplication {

    public static void main(String[] args) {
        EurekaEventHandler.getInstance().init();
        SpringApplication.run(NotifyApplication.class, args);
    }
}

