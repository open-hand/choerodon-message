package io.choerodon.notify.infra.dto;

import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.*;

/**
 * 消息业务类型
 */
@Table(name = "notify_send_setting")
public class SendSettingDTO extends BaseDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private String name;

    private String description;

    @Column(name = "fd_level")
    private String level;

    private String categoryCode;

    @Column(name = "is_allow_config")
    private Boolean isAllowConfig;

    @Column(name = "is_edit")
    private Boolean isEdit;

    @Column(name = "is_enabled")
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


    public SendSettingDTO(String code) {
        this.code = code;
    }

    public SendSettingDTO(String code, String name, String description, String level, String categoryCode, Boolean isAllowConfig, Boolean enabled, Integer retryCount, Boolean isSendInstantly, Boolean isManualRetry, Boolean emailEnabledFlag, Boolean pmEnabledFlag, Boolean smsEnabledFlag, Boolean webhookEnabledFlag, Boolean backlogFlag) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.level = level;
        this.categoryCode = categoryCode;
        this.isAllowConfig = isAllowConfig;
        this.enabled = enabled;
        this.retryCount = retryCount;
        this.isSendInstantly = isSendInstantly;
        this.isManualRetry = isManualRetry;
        this.emailEnabledFlag = emailEnabledFlag;
        this.pmEnabledFlag = pmEnabledFlag;
        this.smsEnabledFlag = smsEnabledFlag;
        this.webhookEnabledFlag = webhookEnabledFlag;
        this.backlogFlag = backlogFlag;
    }

    public SendSettingDTO() {
    }

    @Override
    public String toString() {
        return "SendSettingDTO{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", categoryCode=" + categoryCode +
                ", emailEnabledFlag=" + emailEnabledFlag +
                ", pmEnabledFlag=" + pmEnabledFlag +
                ", level='" + level + '\'' +
                ", smsEnabledFlag='" + smsEnabledFlag + '\'' +
                ", isAllowConfig=" + isAllowConfig +
                ", enabled=" + enabled +
                ", retryCount=" + retryCount +
                ", isSendInstantly=" + isSendInstantly +
                ", isManualRetry=" + isManualRetry +
                ", backlogFlag=" + backlogFlag +
                ", isEdit" + isEdit +
                ", webhookEnabledFlag=" + webhookEnabledFlag +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public SendSettingDTO setCode(String code) {
        this.code = code;
        return this;
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


    public String getLevel() {
        return level;
    }

    public SendSettingDTO setLevel(String level) {
        this.level = level;
        return this;
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

    public Boolean getAllowConfig() {
        return isAllowConfig;
    }

    public void setAllowConfig(Boolean allowConfig) {
        isAllowConfig = allowConfig;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public SendSettingDTO setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryCode() {
        return categoryCode;
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

    public SendSettingDTO setWebhookEnabledFlag(Boolean webhookEnabledFlag) {
        this.webhookEnabledFlag = webhookEnabledFlag;
        return this;
    }

    public Boolean getBacklogFlag() {
        return backlogFlag;
    }

    public void setBacklogFlag(Boolean backlogFlag) {
        this.backlogFlag = backlogFlag;
    }

    public Boolean getEdit() {
        return isEdit;
    }

    public void setEdit(Boolean edit) {
        isEdit = edit;
    }

    public Boolean getSendInstantly() {
        return isSendInstantly;
    }

    public void setSendInstantly(Boolean sendInstantly) {
        isSendInstantly = sendInstantly;
    }

    public Boolean getManualRetry() {
        return isManualRetry;
    }

    public void setManualRetry(Boolean manualRetry) {
        isManualRetry = manualRetry;
    }
}
