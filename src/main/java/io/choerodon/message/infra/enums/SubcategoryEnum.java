package io.choerodon.message.infra.enums;

/**
 * Created by wangxiang on 2021/2/25
 */
public enum SubcategoryEnum {
    /**
     * 环境通知
     */
    ENV_NOTICE("ENV-NOTICE"),
    /**
     * 集群通知
     */
    CLUSTER_NOTICE("CLUSTER-NOTICE"),
    /**
     * 其他
     */
    DEFAULT("DEFAULT"),

    /**
     * 系统通知
     */
    SYS_MANAGEMENT("SYS-MANAGEMENT"),

    /**
     * 账户安全通知
     */
    ACCOUNT_SECURITY_NOTICE("ACCOUNT-SECURITY-NOTICE"),

    /**
     * 注册组织通知
     */
    REGISTER_ORG_NOTICE("REGISTER-ORG-NOTICE"),

    /**
     * 组织消息
     */
    ORG_MANAGEMENT("ORG-MANAGEMENT"),

    /**
     * 项目消息
     */
    PRO_MANAGEMENT("PRO-MANAGEMENT"),

    /**
     * 敏捷通知
     */
    ISSUE_STATUS_CHANGE_NOTICE("ISSUE-STATUS-CHANGE-NOTICE"),
    /**
     * 应用服务通知
     */
    APP_SERVICE_NOTICE("APP-SERVICE-NOTICE"),
    /**
     * 代码管理通知
     */
    CODE_MANAGEMENT_NOTICE("CODE-MANAGEMENT-NOTICE"),
    /**
     * 部署资源通知
     */
    DEPLOYMENT_RESOURCES_NOTICE("DEPLOYMENT-RESOURCES-NOTICE"),
    /**
     * 流水线通知
     */
    STREAM_CHANGE_NOTICE("STREAM-CHANGE-NOTICE"),
    /**
     * 资源删除验证
     */
    RESOURCE_SECURITY_NOTICE("RESOURCE-SECURITY-NOTICE"),
    /**
     * 添加/导入用户通知
     */
    ADD_OR_IMPORT_USER("ADD-OR-IMPORT-USER"),

    /**
     * 需求池通知
     */
    BACKLOG_NOTICE("BACKLOG-NOTICE"),

    /**
     * 应用部署通知
     */
    APP_DEPLOYMENT_NOTICE("APP-DEPLOYMENT-NOTICE"),

    /**
     * 市场应用通知
     */
    MARKET_APP("MARKET_APP"),

    /**
     * 测试通知
     */
    API_TEST_EXECUTE_NOTICE("API-TEST-EXECUTE-NOTICE"),

    /**
     * 评论通知
     */
    ISSUE_COMMENT_NOTICE("ISSUE-COMMENT-NOTICE");

    private String value;

    SubcategoryEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
