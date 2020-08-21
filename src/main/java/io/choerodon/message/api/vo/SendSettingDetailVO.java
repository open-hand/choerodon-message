package io.choerodon.message.api.vo;


import org.hzero.starter.keyencrypt.core.Encrypt;

public class SendSettingDetailVO {
    @Encrypt
    private Long id;

    private String code;

    private String name;

    private String categoryCode;

    private Boolean backlogFlag;

    private String description;

    @Encrypt
    private Long emailTemplateId;

    @Encrypt
    private Long smsTemplateId;

    @Encrypt
    private Long pmTemplateId;

    private String level;

    private Integer retryCount;

    private Boolean isSendInstantly;

    private Boolean isManualRetry;

    private Boolean isAllowConfig;

    private Boolean smsEnabledFlag;
    private Boolean webhookEnabledFlag;
    private Boolean pmEnabledFlag;
    private Boolean emailEnabledFlag;

    private Long objectVersionNumber;

    public Long getId() {
        return id;
    }

    public SendSettingDetailVO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getCode() {
        return code;
    }

    public SendSettingDetailVO setCode(String code) {
        this.code = code;
        return this;
    }

    public String getName() {
        return name;
    }

    public SendSettingDetailVO setName(String name) {
        this.name = name;
        return this;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public SendSettingDetailVO setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
        return this;
    }

    public Boolean getBacklogFlag() {
        return backlogFlag;
    }

    public SendSettingDetailVO setBacklogFlag(Boolean backlogFlag) {
        this.backlogFlag = backlogFlag;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public SendSettingDetailVO setDescription(String description) {
        this.description = description;
        return this;
    }

    public Long getEmailTemplateId() {
        return emailTemplateId;
    }

    public SendSettingDetailVO setEmailTemplateId(Long emailTemplateId) {
        this.emailTemplateId = emailTemplateId;
        return this;
    }

    public Long getSmsTemplateId() {
        return smsTemplateId;
    }

    public SendSettingDetailVO setSmsTemplateId(Long smsTemplateId) {
        this.smsTemplateId = smsTemplateId;
        return this;
    }

    public Long getPmTemplateId() {
        return pmTemplateId;
    }

    public SendSettingDetailVO setPmTemplateId(Long pmTemplateId) {
        this.pmTemplateId = pmTemplateId;
        return this;
    }

    public String getLevel() {
        return level;
    }

    public SendSettingDetailVO setLevel(String level) {
        this.level = level;
        return this;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public SendSettingDetailVO setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
        return this;
    }

    public Boolean getSendInstantly() {
        return isSendInstantly;
    }

    public SendSettingDetailVO setSendInstantly(Boolean sendInstantly) {
        isSendInstantly = sendInstantly;
        return this;
    }

    public Boolean getManualRetry() {
        return isManualRetry;
    }

    public SendSettingDetailVO setManualRetry(Boolean manualRetry) {
        isManualRetry = manualRetry;
        return this;
    }

    public Boolean getAllowConfig() {
        return isAllowConfig;
    }

    public SendSettingDetailVO setAllowConfig(Boolean allowConfig) {
        isAllowConfig = allowConfig;
        return this;
    }

    public Boolean getSmsEnabledFlag() {
        return smsEnabledFlag;
    }

    public SendSettingDetailVO setSmsEnabledFlag(Boolean smsEnabledFlag) {
        this.smsEnabledFlag = smsEnabledFlag;
        return this;
    }

    public Boolean getWebhookEnabledFlag() {
        return webhookEnabledFlag;
    }

    public SendSettingDetailVO setWebhookEnabledFlag(Boolean webhookEnabledFlag) {
        this.webhookEnabledFlag = webhookEnabledFlag;
        return this;
    }

    public Boolean getPmEnabledFlag() {
        return pmEnabledFlag;
    }

    public SendSettingDetailVO setPmEnabledFlag(Boolean pmEnabledFlag) {
        this.pmEnabledFlag = pmEnabledFlag;
        return this;
    }

    public Boolean getEmailEnabledFlag() {
        return emailEnabledFlag;
    }

    public SendSettingDetailVO setEmailEnabledFlag(Boolean emailEnabledFlag) {
        this.emailEnabledFlag = emailEnabledFlag;
        return this;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public SendSettingDetailVO setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
        return this;
    }
}
