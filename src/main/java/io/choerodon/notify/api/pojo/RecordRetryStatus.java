package io.choerodon.notify.api.pojo;

public enum RecordRetryStatus {
    MANUAL_RETRY_FAILED("RETRY_FAILED"),
    MANUAL_RETRY_SUCCESS("RETRY_SUCCEED")
    ;

    private String value;

    RecordRetryStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
