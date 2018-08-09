package io.choerodon.notify.infra.config;

import io.choerodon.notify.domain.Config;
import io.choerodon.notify.infra.utils.ConvertUtils;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.PropertyMap;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "choerodon.notify")
public class NotifyProperties {

    private Map<String, String> businessType = new LinkedHashMap<>();

    private boolean initEmailConfig = true;

    @NestedConfigurationProperty
    private Email email;

    @Getter
    @Setter
    public static class Email {

        private String account;

        private String password;

        private String sendName;

        private String protocol;

        private String host;

        private Integer port;

        private Boolean ssl = false;

        public static PropertyMap<Email, Config> properties2Entity() {
            return new PropertyMap<Email, Config>() {
                @Override
                protected void configure() {
                    using(ConvertUtils.addPrefix("email")).map();
                }
            };
        }
    }

}
