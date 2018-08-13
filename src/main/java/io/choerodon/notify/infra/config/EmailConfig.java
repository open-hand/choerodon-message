package io.choerodon.notify.infra.config;

import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@Import(MailSenderAutoConfiguration.class)
@EnableConfigurationProperties(NotifyProperties.class)
public class EmailConfig {

    @Bean
    public JavaMailSender mailSender() {
        return new JavaMailSenderImpl();
    }


}
