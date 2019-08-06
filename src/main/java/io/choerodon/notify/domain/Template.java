package io.choerodon.notify.domain;



import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 消息模板实体
 * 包括邮件,站内信模板和短信模版
 */
@Table(name = "notify_template")
public class Template extends BaseDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "error.template.code.empty")
    private String code;

    @NotEmpty(message = "error.template.name.empty")
    private String name;

    @NotEmpty(message = "error.template.messageType.empty")
    private String messageType;

    @NotNull(message = "error.template.isPredefined.null")
    private Boolean isPredefined;

    @NotEmpty(message = "error.template.businessType.empty")
    private String businessType;

    private String emailTitle;

    private String emailContent;

    private String pmTitle;

    private String pmContent;

    private String smsContent;

    public Template(String code, String messageType) {
        this.code = code;
        this.messageType = messageType;
    }

    public Template() {
    }

    @Override
    public String toString() {
        return "Template{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", messageType='" + messageType + '\'' +
                ", isPredefined=" + isPredefined +
                ", businessType='" + businessType + '\'' +
                ", emailTitle='" + emailTitle + '\'' +
                ", emailContent='" + emailContent + '\'' +
                ", pmTitle='" + pmTitle + '\'' +
                ", pmContent='" + pmContent + '\'' +
                ", smsContent='" + smsContent + '\'' +
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

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public Boolean getIsPredefined() {
        return isPredefined;
    }

    public void setIsPredefined(Boolean predefined) {
        isPredefined = predefined;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getEmailTitle() {
        return emailTitle;
    }

    public void setEmailTitle(String emailTitle) {
        this.emailTitle = emailTitle;
    }

    public String getEmailContent() {
        return emailContent;
    }

    public void setEmailContent(String emailContent) {
        this.emailContent = emailContent;
    }

    public String getSmsContent() {
        return smsContent;
    }

    public void setSmsContent(String smsContent) {
        this.smsContent = smsContent;
    }

    public String getPmTitle() {
        return pmTitle;
    }

    public void setPmTitle(String pmTitle) {
        this.pmTitle = pmTitle;
    }

    public String getPmContent() {
        return pmContent;
    }

    public void setPmContent(String pmContent) {
        this.pmContent = pmContent;
    }
}
