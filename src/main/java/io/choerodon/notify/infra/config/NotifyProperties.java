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

    private static final String VAR_DATASOURCE = "datasource";

    private boolean initEmailConfig = true;

    private Map<String, BusinessType> types = new LinkedHashMap<>();

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

    @Getter
    @Setter
    public static class BusinessType {

        private String name;

        private String description;

        private Integer retryCount = 0;

        private Boolean isSendInstantly = true;

        private Boolean isManualRetry = false;

    }


}
