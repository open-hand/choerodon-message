package io.choerodon.notify.api.pojo;

/**
 * User: Mr.Wang
 * Date: 2019/12/3
 * 消息通知的类型
 */
public enum NotifyType {
    AGILE_NOTIFY("agileNotify"),
    DEVOPS_NOTIFY("devopsNotify");

    private String typeName;

    NotifyType(String typeName) {
        this.typeName = typeName;
    }

    /**
     * 根据类型的名称，返回类型的枚举实例。
     *
     * @param typeName 类型名称
     */
    public static NotifyType fromTypeName(String typeName) {
        for (NotifyType type : NotifyType.values()) {
            if (type.getTypeName().equals(typeName)) {
                return type;
            }
        }
        return null;
    }

    public String getTypeName() {
        return this.typeName;
    }


}
