package io.choerodon.notify.api.pojo;

/**
 * @author dengyouquan
 **/
public enum PmType {
    MSG("msg"),
    NOTICE("notice");
    private String value;

    public String getValue() {
        return value;
    }

    PmType(String value) {
        this.value = value;
    }
}
