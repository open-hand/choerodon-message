package io.choerodon.message.api.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.message.domain.entity.MessageTemplate;
import org.hzero.message.domain.entity.TemplateServer;
import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.List;

/**
 * @author jiameng.cao
 * @date 2019/10/30
 */
@JsonInclude
public class SendSettingVO extends TemplateServer {

    @ApiModelProperty(value = "邮件重发次数")
    private Integer retryCount;

    private Integer emailEnabledFlag;

    private Integer pmEnabledFlag;

    private Integer smsEnabledFlag;

    @ApiModelProperty(value = "钉钉")
    private Integer dtEnabledFlag;

    private Integer webhookEnabledFlag;

    private Integer webhookJsonEnabledFlag;

    @Encrypt
    private Long emailTemplateId;

    @Encrypt
    private Long smsTemplateId;

    @Encrypt
    private Long pmTemplateId;
    private String sendingType;
    private Integer edit;

    public Integer getEdit() {
        return edit;
    }

    public void setEdit(Integer edit) {
        this.edit = edit;
    }

    public String getSendingType() {
        return sendingType;
    }

    public void setSendingType(String sendingType) {
        this.sendingType = sendingType;
    }

    private List<MessageTemplateVO> messageTemplateVOS;

    public Integer getEmailEnabledFlag() {
        return emailEnabledFlag;
    }

    public void setEmailEnabledFlag(Integer emailEnabledFlag) {
        this.emailEnabledFlag = emailEnabledFlag;
    }

    public Integer getPmEnabledFlag() {
        return pmEnabledFlag;
    }

    public void setPmEnabledFlag(Integer pmEnabledFlag) {
        this.pmEnabledFlag = pmEnabledFlag;
    }

    public Integer getSmsEnabledFlag() {
        return smsEnabledFlag;
    }

    public void setSmsEnabledFlag(Integer smsEnabledFlag) {
        this.smsEnabledFlag = smsEnabledFlag;
    }

    public Integer getWebhookEnabledFlag() {
        return webhookEnabledFlag;
    }

    public void setWebhookEnabledFlag(Integer webhookEnabledFlag) {
        this.webhookEnabledFlag = webhookEnabledFlag;
    }

    public Integer getWebhookJsonEnabledFlag() {
        return webhookJsonEnabledFlag;
    }

    public void setWebhookJsonEnabledFlag(Integer webhookJsonEnabledFlag) {
        this.webhookJsonEnabledFlag = webhookJsonEnabledFlag;
    }

    public List<MessageTemplateVO> getMessageTemplateVOS() {
        return messageTemplateVOS;
    }

    public void setMessageTemplateVOS(List<MessageTemplateVO> messageTemplateVOS) {
        this.messageTemplateVOS = messageTemplateVOS;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
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

    public Integer getDtEnabledFlag() {
        return dtEnabledFlag;
    }

    public void setDtEnabledFlag(Integer dtEnabledFlag) {
        this.dtEnabledFlag = dtEnabledFlag;
    }
}
