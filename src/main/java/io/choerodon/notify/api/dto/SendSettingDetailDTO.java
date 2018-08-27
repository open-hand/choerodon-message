package io.choerodon.notify.api.dto;


public class SendSettingDetailDTO {

    private Long id;

    private String code;

    private String name;

    private String description;

    private String emailTemplateCode;

    private Long emailTemplateId;

    private String smsTemplateCode;

    private Long smsTemplateId;

    private String pmTemplateCode;

    private Long pmTemplateId;

    private String level;

    private Integer retryCount;

    private Boolean isSendInstantly;

    private Boolean isManualRetry;

    private Long objectVersionNumber;


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

    public String getEmailTemplateCode() {
        return emailTemplateCode;
    }

    public void setEmailTemplateCode(String emailTemplateCode) {
        this.emailTemplateCode = emailTemplateCode;
    }

    public Long getEmailTemplateId() {
        return emailTemplateId;
    }

    public void setEmailTemplateId(Long emailTemplateId) {
        this.emailTemplateId = emailTemplateId;
    }

    public String getSmsTemplateCode() {
        return smsTemplateCode;
    }

    public void setSmsTemplateCode(String smsTemplateCode) {
        this.smsTemplateCode = smsTemplateCode;
    }

    public Long getSmsTemplateId() {
        return smsTemplateId;
    }

    public void setSmsTemplateId(Long smsTemplateId) {
        this.smsTemplateId = smsTemplateId;
    }

    public String getPmTemplateCode() {
        return pmTemplateCode;
    }

    public void setPmTemplateCode(String pmTemplateCode) {
        this.pmTemplateCode = pmTemplateCode;
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

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
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

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}
