package io.choerodon.notify.infra.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.List;

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

    public boolean isInitSpringEmailConfig() {
        return initSpringEmailConfig;
    }

    public void setInitSpringEmailConfig(boolean initSpringEmailConfig) {
        this.initSpringEmailConfig = initSpringEmailConfig;
    }

    public List<String> getSkipServices() {
        return skipServices;
    }

    public void setSkipServices(List<String> skipServices) {
        this.skipServices = skipServices;
    }

    public Integer getFetchTime() {
        return fetchTime;
    }

    public void setFetchTime(Integer fetchTime) {
        this.fetchTime = fetchTime;
    }

    public Boolean getLocal() {
        return isLocal;
    }

    public void setLocal(Boolean local) {
        isLocal = local;
    }

    public Integer getAsynSendNoticesThreadNum() {
        return asynSendNoticesThreadNum;
    }

    public void setAsynSendNoticesThreadNum(Integer asynSendNoticesThreadNum) {
        this.asynSendNoticesThreadNum = asynSendNoticesThreadNum;
    }


}
