package io.choerodon.notify.api.dto;

import io.choerodon.notify.infra.dto.Template;

import javax.persistence.Column;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author jiameng.cao
 * @date 2019/10/30
 */
public class SendSettingVO {
    private Long id;

    private String code;

    private String name;

    private String description;

    private String categoryCode;

    @Column(name = "is_allow_config")
    private Boolean isAllowConfig;

    @ApiModelProperty(value = "是否允许更改，允许配置接收")
    private Boolean edit;

    private Boolean enabled;

    private Integer retryCount;

    @Column(name = "is_send_instantly")
    private Boolean isSendInstantly;

    @Column(name = "is_manual_retry")
    private Boolean isManualRetry;

    private Boolean emailEnabledFlag;

    private Boolean pmEnabledFlag;

    private Boolean smsEnabledFlag;

    private Boolean webhookEnabledFlag;

    private Boolean backlogFlag;

    private List<Template> templates;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public Boolean getAllowConfig() {
        return isAllowConfig;
    }

    public void setAllowConfig(Boolean allowConfig) {
        isAllowConfig = allowConfig;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Boolean getIsSendInstantly() {
        return isSendInstantly;
    }

    public void setIsSendInstantly(Boolean sendInstantly) {
        isSendInstantly = sendInstantly;
    }

    public Boolean getIsManualRetry() {
        return isManualRetry;
    }

    public void setIsManualRetry(Boolean manualRetry) {
        isManualRetry = manualRetry;
    }

    public Boolean getEmailEnabledFlag() {
        return emailEnabledFlag;
    }

    public void setEmailEnabledFlag(Boolean emailEnabledFlag) {
        this.emailEnabledFlag = emailEnabledFlag;
    }

    public Boolean getPmEnabledFlag() {
        return pmEnabledFlag;
    }

    public void setPmEnabledFlag(Boolean pmEnabledFlag) {
        this.pmEnabledFlag = pmEnabledFlag;
    }

    public Boolean getSmsEnabledFlag() {
        return smsEnabledFlag;
    }

    public void setSmsEnabledFlag(Boolean smsEnabledFlag) {
        this.smsEnabledFlag = smsEnabledFlag;
    }

    public Boolean getWebhookEnabledFlag() {
        return webhookEnabledFlag;
    }

    public void setWebhookEnabledFlag(Boolean webhookEnabledFlag) {
        this.webhookEnabledFlag = webhookEnabledFlag;
    }

    public Boolean getBacklogFlag() {
        return backlogFlag;
    }

    public void setBacklogFlag(Boolean backlogFlag) {
        this.backlogFlag = backlogFlag;
    }

    public List<Template> getTemplates() {
        return templates;
    }

    public void setTemplates(List<Template> templates) {
        this.templates = templates;
    }

    public Boolean getEdit() {
        return edit;
    }

    public void setEdit(Boolean edit) {
        this.edit = edit;
    }
}
