package io.choerodon.notify.api.pojo;

public enum MessageType {
    EMAIL("email"),
    SMS("sms"),
    PM("pm"),
    WH("wh");

    private String value;

    MessageType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static boolean isInclude(String key) {
        boolean include = false;
        for (MessageType e : MessageType.values()) {
            if (e.getValue().equalsIgnoreCase(key)) {
                include = true;
                break;
            }
        }
        return include;
    }
}
