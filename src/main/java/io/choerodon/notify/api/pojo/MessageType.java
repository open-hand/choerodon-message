package io.choerodon.notify.api.pojo;

public enum MessageType {
    EMAIL("email"), SMS("sms"), PM("pm");

    private String value;

    MessageType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
