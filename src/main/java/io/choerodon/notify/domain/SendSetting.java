package io.choerodon.notify.domain;

import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.*;

/**
 * 消息业务类型
 */
@Table(name = "notify_send_setting")
public class SendSetting extends BaseDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private String name;

    private String description;

    private Long emailTemplateId;

    private Long smsTemplateId;

    private Long pmTemplateId;

    @Column(name = "fd_level")
    private String level;

    private String pmType;

    private Boolean isAllowConfig;

    @Column(name = "is_enabled")
    private Boolean enabled;

    private Integer retryCount;

    private Boolean isSendInstantly;

    private Boolean isManualRetry;

    private Long whTemplateId;

    public SendSetting(String code) {
        this.code = code;
    }

    public SendSetting(String code, String name, String description,
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

    public SendSetting() {
    }

    @Override
    public String toString() {
        return "SendSetting{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", emailTemplateId=" + emailTemplateId +
                ", smsTemplateId=" + smsTemplateId +
                ", pmTemplateId=" + pmTemplateId +
                ", level='" + level + '\'' +
                ", pmType='" + pmType + '\'' +
                ", isAllowConfig=" + isAllowConfig +
                ", enabled=" + enabled +
                ", retryCount=" + retryCount +
                ", isSendInstantly=" + isSendInstantly +
                ", isManualRetry=" + isManualRetry +
                ", whTemplateId=" + whTemplateId +
                '}';
    }

    public Long getWhTemplateId() {
        return whTemplateId;
    }

    public void setWhTemplateId(Long whTemplateId) {
        this.whTemplateId = whTemplateId;
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

    public Long getEmailTemplateId() {
        return emailTemplateId;
    }

    public void setEmailTemplateId(Long emailTemplateId) {
        this.emailTemplateId = emailTemplateId;
    }

    public Long getSmsTemplateId() {
        return smsTemplateId;
    }

    public void setSmsTemplateId(Long smsTemplateId) {
        this.smsTemplateId = smsTemplateId;
    }

    public Long getPmTemplateId() {
        return pmTemplateId;
    }

    public void setPmTemplateId(Long pmTemplateId) {
        this.pmTemplateId = pmTemplateId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getPmType() {
        return pmType;
    }

    public void setPmType(String pmType) {
        this.pmType = pmType;
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
}
