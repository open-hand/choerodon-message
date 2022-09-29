package io.choerodon.message.infra.config;

import org.hzero.core.message.MessageAccessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
@EnableScheduling
public class ScheduleConfig {

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(5);
        taskScheduler.initialize();
        return taskScheduler;
    }

    @Bean
    @Primary
    public LocalValidatorFactoryBean getValidator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        ReloadableResourceBundleMessageSource messageSource
                = new ReloadableResourceBundleMessageSource();
        messageSource.addBasenames(MessageAccessor.getBasenames());
        messageSource.addBasenames("classpath:messages/messages_bus");
        messageSource.setDefaultEncoding("UTF-8");
        bean.setValidationMessageSource(messageSource);
        return bean;
    }
}
