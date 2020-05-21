package io.choerodon.message.infra.enums;

public enum SendingTypeEnum {
    EMAIL("EMAIL"),
    SMS("SMS"),
    WEB("WEB"),
    WEB_HOOK("WEB_HOOK");

    private String value;

    SendingTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static boolean isInclude(String key) {
        boolean include = false;
        for (SendingTypeEnum e : SendingTypeEnum.values()) {
            if (e.getValue().equalsIgnoreCase(key)) {
                include = true;
                break;
            }
        }
        return include;
    }
}
