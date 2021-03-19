package io.choerodon.message;

import org.hzero.autoconfigure.message.EnableHZeroMessage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableHZeroMessage
@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
@EnableAsync
public class MessageApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(MessageApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


