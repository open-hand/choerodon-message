package io.choerodon.notify.infra.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

/**
 * @author bgzyy
 * @since 2019/9/11
 */
@Table(name = "NOTIFY_WEBHOOK")
public class WebHookDTO {
    public static final String WEB_HOOK_TYPE_DING_TALK = "DingTalk";
    public static final String WEB_HOOK_TYPE_WE_CHAT = "WeChat";
    public static final String WEB_HOOK_TYPE_JSON = "Json";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("webhook名称")
    private String name;

    @ApiModelProperty("webhook类型/必填字段")
    private String type;

    @ApiModelProperty("webhook地址/必填字段")
    private String webhookPath;

    @ApiModelProperty("项目ID/必填字段")
    private Long projectId;

    @ApiModelProperty("webhook是否启用")
    private Boolean enableFlag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWebhookPath() {
        return webhookPath;
    }

    public void setWebhookPath(String webhookPath) {
        this.webhookPath = webhookPath;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getEnableFlag() {
        return enableFlag;
    }

    public void setEnableFlag(Boolean enableFlag) {
        this.enableFlag = enableFlag;
    }
}