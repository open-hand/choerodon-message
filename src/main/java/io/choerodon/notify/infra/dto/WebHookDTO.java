package io.choerodon.notify.infra.dto;

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
    @GeneratedValue
    private Long id;
    private String name;
    private String type;
    private String webhookPath;
    private Long projectId;
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