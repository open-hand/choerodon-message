package io.choerodon.notify.domain;

import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

@Table(name = "notify_sms_config")
public class SmsConfigDTO extends BaseDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long organizationId;

    @NotEmpty(message = "error.msmConfig.signature.empty")
    private String signature;

    @NotEmpty(message = "error.msmConfig.hostAddress.empty")
    private String hostAddress;

    private String hostPort;

    @NotEmpty(message = "error.msmConfig.sendType.empty")
    private String sendType;

    private String singleSendApi;

    private String batchSendApi;

    private String asyncSendApi;

    @NotEmpty(message = "error.msmConfig.sendType.empty")
    private String secretKey;

    private String contentField;

    private String phoneField;

    private String signatureField;

    private String businessCodeField;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    public String getHostPort() {
        return hostPort;
    }

    public void setHostPort(String hostPort) {
        this.hostPort = hostPort;
    }

    public String getAsyncSendApi() {
        return asyncSendApi;
    }

    public void setAsyncSendApi(String asyncSendApi) {
        this.asyncSendApi = asyncSendApi;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getContentField() {
        return contentField;
    }

    public void setContentField(String contentField) {
        this.contentField = contentField;
    }

    public String getPhoneField() {
        return phoneField;
    }

    public void setPhoneField(String phoneField) {
        this.phoneField = phoneField;
    }

    public String getSignatureField() {
        return signatureField;
    }

    public void setSignatureField(String signatureField) {
        this.signatureField = signatureField;
    }

    public String getBusinessCodeField() {
        return businessCodeField;
    }

    public void setBusinessCodeField(String businessCodeField) {
        this.businessCodeField = businessCodeField;
    }

    public String getSendType() {
        return sendType;
    }

    public void setSendType(String sendType) {
        this.sendType = sendType;
    }

    public String getSingleSendApi() {
        return singleSendApi;
    }

    public void setSingleSendApi(String singleSendApi) {
        this.singleSendApi = singleSendApi;
    }

    public String getBatchSendApi() {
        return batchSendApi;
    }

    public void setBatchSendApi(String batchSendApi) {
        this.batchSendApi = batchSendApi;
    }
}
