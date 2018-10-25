package io.choerodon.notify.api.dto;


public class SendSettingListDTO {

    private Long id;

    private String code;

    private String name;

    private String level;

    private String pmType;

    private String description;

    private String emailTemplateCode;

    private String smsTemplateCode;

    private String pmTemplateCode;

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

    public String getSmsTemplateCode() {
        return smsTemplateCode;
    }

    public void setSmsTemplateCode(String smsTemplateCode) {
        this.smsTemplateCode = smsTemplateCode;
    }

    public String getPmTemplateCode() {
        return pmTemplateCode;
    }

    public void setPmTemplateCode(String pmTemplateCode) {
        this.pmTemplateCode = pmTemplateCode;
    }
}
