package io.choerodon.notify.domain;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 邮箱服务器，短信服务器的配置
 */
@ModifyAudit
@VersionAudit
@Table(name = "notify_config")
public class Config extends AuditDomain {

    public static final String EMAIL_PROTOCOL_SMTP = "SMTP";
    public static final String EMAIL_SSL_SMTP = "mail.smtp.ssl.enable";

    @Id
    @GeneratedValue
    private Long id;
    private String emailAccount;
    private String emailPassword;
    private String emailSendName;
    private String emailProtocol;
    private String emailHost;
    private Integer emailPort;
    private Boolean emailSsl;
    private String smsDomain;
    private String smsKeyId;
    private String smsKeyPassword;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmailAccount() {
        return emailAccount;
    }

    public void setEmailAccount(String emailAccount) {
        this.emailAccount = emailAccount;
    }

    public String getEmailPassword() {
        return emailPassword;
    }

    public void setEmailPassword(String emailPassword) {
        this.emailPassword = emailPassword;
    }

    public String getEmailSendName() {
        return emailSendName;
    }

    public void setEmailSendName(String emailSendName) {
        this.emailSendName = emailSendName;
    }

    public String getEmailProtocol() {
        return emailProtocol;
    }

    public void setEmailProtocol(String emailProtocol) {
        this.emailProtocol = emailProtocol;
    }

    public String getEmailHost() {
        return emailHost;
    }

    public void setEmailHost(String emailHost) {
        this.emailHost = emailHost;
    }

    public Integer getEmailPort() {
        return emailPort;
    }

    public void setEmailPort(Integer emailPort) {
        this.emailPort = emailPort;
    }

    public Boolean getIsEmailSsl() {
        return emailSsl;
    }

    public void setIsEmailSsl(Boolean emailSsl) {
        this.emailSsl = emailSsl;
    }

    public String getSmsDomain() {
        return smsDomain;
    }

    public void setSmsDomain(String smsDomain) {
        this.smsDomain = smsDomain;
    }

    public String getSmsKeyId() {
        return smsKeyId;
    }

    public void setSmsKeyId(String smsKeyId) {
        this.smsKeyId = smsKeyId;
    }

    public String getSmsKeyPassword() {
        return smsKeyPassword;
    }

    public void setSmsKeyPassword(String smsKeyPassword) {
        this.smsKeyPassword = smsKeyPassword;
    }
}
