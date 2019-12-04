package io.choerodon.notify.api.pojo;

/**
 * User: Mr.Wang
 * Date: 2019/12/4
 */
public enum TargetUserType {
    /**
     * 报告人
     */
    REPORTER("reporter"),
    /**
     * 经办人
     */
    ASSIGNEE("assignee"),
    /**
     * 指定用户
     */
    SELECTED_USERS("selectedUser"),
    /**
     * 创建者
     */
    CREATOR("creator"),
    /**
     * 应用服务权限拥有者
     */
    APPLICATION_SERVICE_PERMISSION_OWNER(" applicationServicePermissionOwner"),
    /**
     * 代码提交者
     */
    CODE_SUBMITTEDER("codeSubmitteder"),
    /**
     * 合并请求创建者
     */
    MERGE_REQUEST_CREATOR("mergeRequestCreator"),

    /**
     * 实例部署人员
     */
    INSTANCE_DEPLOYER("instanceDeployer"),

    /**
     * 网络创建者
     */
    NETWORK_CREATOR("networkCreator"),

    /**
     * 域名创建者
     */
    DOMAIN_NAME_CREATOR("domainNameCreator"),
    /**
     * 证书创建者
     */
    CERTIFICATE_CREATOR("certificateCreator"),

    /**
     * PVC创建者
     */
    PVC_CREATOR("pvcCreator"),
    /**
     * 流水线创建者
     */
    ASSEMBLY_LINE_CREATOR("assemblyLineCreator"),
    /**
     * 流水线触发者
     */
    ASSEMBLY_LINE_TRIGGERS("assemblyLineTriggers");
    private String typeName;

    TargetUserType(String typeName) {
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
