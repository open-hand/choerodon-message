package io.choerodon.notify.infra.dto;

import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.*;

/**
 * 消息业务类型
 */

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

    private Boolean isAllowConfig;

    @Column(name = "is_enabled")
    private Boolean enabled;

    private Integer retryCount;

    private Boolean isSendInstantly;

    private Boolean isManualRetry;

    private Boolean emailEnabledFlag;

    private Boolean pmEnabledFlag;

    private Boolean smsEnabledFlag;

    private Boolean webhookEnabledFlag;

    private Boolean backlogGFlag;


    public SendSettingDTO(String code) {
        this.code = code;
    }

    public SendSettingDTO(String code, String name, String description,
                          String level, Integer retryCount,
                          Boolean isSendInstantly, Boolean isManualRetry) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.level = level;
        this.retryCount = retryCount;
        this.isSendInstantly = isSendInstantly;
        this.isManualRetry = isManualRetry;
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
                ", backlogGFlag=" + backlogGFlag +
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


    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
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

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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

    public void setWebhookEnabledFlag(Boolean webhookEnabledFlag) {
        this.webhookEnabledFlag = webhookEnabledFlag;
    }

    public Boolean getBacklogGFlag() {
        return backlogGFlag;
    }

    public void setBacklogGFlag(Boolean backlogGFlag) {
        this.backlogGFlag = backlogGFlag;
    }
}
