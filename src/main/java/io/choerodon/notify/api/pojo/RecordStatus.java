package io.choerodon.notify.api.pojo;

public enum RecordStatus {
    COMPLETE("COMPLETED"),
    FAILED("FAILED");
    private String value;

    RecordStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}