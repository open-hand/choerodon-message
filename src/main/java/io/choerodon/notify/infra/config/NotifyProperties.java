package io.choerodon.notify.infra.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "choerodon.notify")
public class NotifyProperties {

    public static final String LEVEL_SITE = "site";

    public static final String LEVEL_ORG = "organization";

    private boolean initSpringEmailConfig = true;

    private Integer asynSendNoticesThreadNum = 5;

    public boolean isInitSpringEmailConfig() {
        return initSpringEmailConfig;
    }

    public void setInitSpringEmailConfig(boolean initSpringEmailConfig) {
        this.initSpringEmailConfig = initSpringEmailConfig;
    }

    public Integer getAsynSendNoticesThreadNum() {
        return asynSendNoticesThreadNum;
    }

    public void setAsynSendNoticesThreadNum(Integer asynSendNoticesThreadNum) {
        this.asynSendNoticesThreadNum = asynSendNoticesThreadNum;
    }


}
