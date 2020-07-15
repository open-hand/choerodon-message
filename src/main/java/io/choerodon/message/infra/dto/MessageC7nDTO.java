package io.choerodon.message.infra.dto;

import java.util.Date;

import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.starter.keyencrypt.core.Encrypt;

public class MessageC7nDTO {
    @Encrypt
    private Long id;
    private String email;
    private String messageCode;
    private String messageName;
    private String failedReason;
    @LovValue(
            lovCode = "HMSG.TRANSACTION_STATUS"
    )
    private String statusCode;

    private String statusMeaning;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMeaning() {
        return statusMeaning;
    }

    public void setStatusMeaning(String statusMeaning) {
        this.statusMeaning = statusMeaning;
    }

    private Date creationDate;
    private String webhookAddress;

    public String getWebhookAddress() {
        return webhookAddress;
    }

    public void setWebhookAddress(String webhookAddress) {
        this.webhookAddress = webhookAddress;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    public String getMessageName() {
        return messageName;
    }

    public void setMessageName(String messageName) {
        this.messageName = messageName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getFailedReason() {
        return failedReason;
    }

    public void setFailedReason(String failedReason) {
        this.failedReason = failedReason;
    }

    public Long getId() {
        return id;
    }

    public MessageC7nDTO setId(Long id) {
        this.id = id;
        return this;
    }
}
