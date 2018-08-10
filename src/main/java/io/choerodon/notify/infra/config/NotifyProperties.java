package io.choerodon.notify.infra.config;

import lombok.Getter;
import lombok.Setter;
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

    }

}
