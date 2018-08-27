package io.choerodon.notify.domain;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.notify.api.pojo.RecordSendData;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@ModifyAudit
@VersionAudit
@Table(name = "notify_record")
public class Record extends AuditDomain {

    @Id
    @GeneratedValue
    private Long id;
    private String status;
    private String receiveAccount;
    private String failedReason;
    private String businessType;
    private String retryStatus;
    private String messageType;
    private String variables;

    @Transient
    private RecordSendData sendData;

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", receiveAccount='" + receiveAccount + '\'' +
                ", failedReason='" + failedReason + '\'' +
                ", messageType='" + messageType + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReceiveAccount() {
        return receiveAccount;
    }

    public void setReceiveAccount(String receiveAccount) {
        this.receiveAccount = receiveAccount;
    }

    public String getFailedReason() {
        return failedReason;
    }

    public void setFailedReason(String failedReason) {
        this.failedReason = failedReason;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getRetryStatus() {
        return retryStatus;
    }

    public void setRetryStatus(String retryStatus) {
        this.retryStatus = retryStatus;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getVariables() {
        return variables;
    }

    public void setVariables(String variables) {
        this.variables = variables;
    }

    public RecordSendData getSendData() {
        return sendData;
    }

    public void setSendData(RecordSendData sendData) {
        this.sendData = sendData;
    }
}
