package io.choerodon.notify.infra.dto;

import io.choerodon.mybatis.entity.BaseDTO;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

/**
 * @author bgzyy
 * @since 2019/9/11
 */
@Table(name = "NOTIFY_WEBHOOK")
public class WebHookDTO extends BaseDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("webhook名称")
    @NotEmpty(message = "error.web.hook.create.or.update.name.can.not.be.empty")
    private String name;

    @NotEmpty(message = "error.web.hook.create.or.update.type.can.not.be.empty")
    @ApiModelProperty("webhook类型/必填字段")
    private String type;

    @ApiModelProperty("webhook地址/必填字段")
    @NotEmpty(message = "error.web.hook.create.or.update.path.can.not.be.empty")
    private String webhookPath;

    @ApiModelProperty("项目ID/必填字段")
    private Long projectId;

    @ApiModelProperty("webhook是否启用")
    private Boolean enableFlag;

    public Long getId() {
        return id;
    }

    public WebHookDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getWebhookPath() {
        return webhookPath;
    }

    public WebHookDTO setWebhookPath(String webhookPath) {
        this.webhookPath = webhookPath;
        return this;
    }

    public Long getProjectId() {
        return projectId;
    }

    public WebHookDTO setProjectId(Long projectId) {
        this.projectId = projectId;
        return this;
    }

    public String getName() {
        return name;
    }

    public WebHookDTO setName(String name) {
        this.name = name;
        return this;
    }

    public String getType() {
        return type;
    }

    public WebHookDTO setType(String type) {
        this.type = type;
        return this;
    }

    public Boolean getEnableFlag() {
        return enableFlag;
    }

    public WebHookDTO setEnableFlag(Boolean enableFlag) {
        this.enableFlag = enableFlag;
        return this;
    }
}