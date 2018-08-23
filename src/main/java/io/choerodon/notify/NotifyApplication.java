package io.choerodon.notify;

import io.choerodon.resource.annoation.EnableChoerodonResourceServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableEurekaClient
@EnableChoerodonResourceServer
public class NotifyApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotifyApplication.class, args);
    }

}

