package io.choerodon.notify.infra.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "choerodon.notify")
public class NotifyProperties {

    public static final String LEVEL_SITE = "site";

    public static final String LEVEL_ORG = "organization";

    private boolean initSpringEmailConfig = true;

    private List<String> skipServices = Arrays.asList("register-server", "api-gateway",
            "gateway-helper", "oauth-server", "config-server", "event-store-service");

    private Integer fetchTime = 5;

    private Boolean isLocal = false;

    private Integer asynSendNoticesThreadNum = 5;

    private Map<String, BusinessType> types = new LinkedHashMap<>();

    @Getter
    @Setter
    public static class BusinessType {

        private String name;

        private String description;

        private Integer retryCount = 0;

        private String level = LEVEL_SITE;

        private Boolean isSendInstantly = true;

        private Boolean isManualRetry = false;

    }


}
