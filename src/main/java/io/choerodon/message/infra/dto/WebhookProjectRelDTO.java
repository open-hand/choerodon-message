package io.choerodon.message.infra.dto;

import javax.persistence.Table;

import io.choerodon.mybatis.domain.AuditDomain;

/**
 * @author scp
 * @date 2020/5/12
 * @description
 */
@Table(name = "webhook_project_rel")
public class WebhookProjectRelDTO extends AuditDomain {
    private Long id;
    private Long webhookId;
    private Long projectId;


    public WebhookProjectRelDTO(Long webhookId, Long projectId) {
        this.webhookId = webhookId;
        this.projectId = projectId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWebhookId() {
        return webhookId;
    }

    public void setWebhookId(Long webhookId) {
        this.webhookId = webhookId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
