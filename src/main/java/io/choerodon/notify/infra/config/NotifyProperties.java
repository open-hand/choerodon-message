package io.choerodon.notify.infra.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    public Map<String, BusinessType> getTypes() {
        return types;
    }

    public void setTypes(Map<String, BusinessType> types) {
        this.types = types;
    }

    public static class BusinessType {

        private String name;

        private String description;

        private Integer retryCount = 0;

        private String level = LEVEL_SITE;

        private Boolean isSendInstantly = true;

        private Boolean isManualRetry = false;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Integer getRetryCount() {
            return retryCount;
        }

        public void setRetryCount(Integer retryCount) {
            this.retryCount = retryCount;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public Boolean getSendInstantly() {
            return isSendInstantly;
        }

        public void setSendInstantly(Boolean sendInstantly) {
            isSendInstantly = sendInstantly;
        }

        public Boolean getManualRetry() {
            return isManualRetry;
        }

        public void setManualRetry(Boolean manualRetry) {
            isManualRetry = manualRetry;
        }
    }


}
