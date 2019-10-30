package io.choerodon.notify.infra.enums;

/**
 * @author jiameng.cao
 * @date 2019/10/29
 */
public enum LevelType {
    /**
     * 平台层
     */
    SITE("平台层"),

    /**
     * 组织
     */
    ORGANIZATION("组织层"),

    /**
     * 项目
     */
    PROJECT("项目层");

    private String value;

    LevelType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
