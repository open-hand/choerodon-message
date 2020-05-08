package io.choerodon.message.infra;

import java.util.HashMap;

/**
 * @author scp
 * @date 2020/4/28
 * @description
 */
public enum ConfigNameEnum {
    EMAIL_NAME("choerodon-email"),
    SMS_NAME("choerodon-sms"),
    WEBHOOK_JSON_NAME("choerodon-webhook-json"),
    WEBHOOK_NAME("choerodon-webhook");


    private String value;

    public String value() {
        return value;
    }

    ConfigNameEnum(String value) {
        this.value = value;
    }


    public static HashMap<String, String> configNames = new HashMap<>(2);

    static {
        configNames.put(ConfigNameEnum.EMAIL_NAME.value, "Choerodon平台-邮件账户");
        configNames.put(ConfigNameEnum.SMS_NAME.value, "Choerodon平台-短信账户");
    }
}
