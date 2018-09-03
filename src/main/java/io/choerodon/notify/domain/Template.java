package io.choerodon.notify.domain;


import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 消息模板实体
 * 包括邮件模板和短信模版
 */
@ModifyAudit
@VersionAudit
@Table(name = "notify_template")
public class Template extends AuditDomain {

    @Id
    @GeneratedValue
    private Long id;

    private String code;

    private String name;

    private String messageType;

    private Boolean isPredefined;

    private String businessType;

    private String emailTitle;

    private String emailContent;

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
}
