package io.choerodon.message.infra.enums;

import java.util.HashMap;

/**
 * @author scp
 * @date 2020/4/28
 * @description
 */
public enum ConfigNameEnum {
    EMAIL_NAME("CHOERODON-EMAIL"),
    SMS_NAME("CHOERODON-SMS");


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
