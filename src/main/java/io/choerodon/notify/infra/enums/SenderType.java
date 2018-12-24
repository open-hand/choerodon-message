package io.choerodon.notify.infra.enums;

/**
 * @author superlee
 */
public enum SenderType {

    /**
     * 全局
     */
    SITE("site"),

    /**
     * 组织
     */
    ORGANIZATION("organization"),

    /**
     * 项目
     */
    PROJECT("project"),

    /**
     * 用户
     */
    USER("user");

    private String value;

    SenderType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
