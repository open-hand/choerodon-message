package io.choerodon.message.infra.enums;

public enum WebHookTypeEnum {
    DINGTALK("DINGTALK"),
    WECHAT("WECHAT"),
    JSON("JSON");

    private String value;

    WebHookTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static boolean isInclude(String key) {
        boolean include = false;
        for (WebHookTypeEnum e : WebHookTypeEnum.values()) {
            if (e.getValue().equalsIgnoreCase(key)) {
                include = true;
                break;
            }
        }
        return include;
    }
}
