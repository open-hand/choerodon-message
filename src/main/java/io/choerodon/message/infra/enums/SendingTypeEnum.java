package io.choerodon.message.infra.enums;

import java.util.HashMap;

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

    private static HashMap<String, SendingTypeEnum> valuesMap = new HashMap<>(4);

    static {
        SendingTypeEnum[] var0 = values();

        for (SendingTypeEnum accessLevel : var0) {
            valuesMap.put(accessLevel.value, accessLevel);
        }

    }



    /**
     * 根据string类型返回枚举类型
     *
     * @param value String
     */
    public static SendingTypeEnum forValue(String value) {
        return valuesMap.get(value);
    }
}
