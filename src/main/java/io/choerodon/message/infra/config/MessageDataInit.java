package io.choerodon.message.infra.config;

import org.hzero.core.message.MessageAccessor;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * @author scp
 * @date 2020/11/6
 * @description
 */
@Component
public class MessageDataInit implements SmartInitializingSingleton {
    @Autowired
    private LocalValidatorFactoryBean localValidatorFactoryBean;


    @Override
    public void afterSingletonsInstantiated() {
        MessageAccessor.addBasenames("classpath:messages/messages");
    }
}
