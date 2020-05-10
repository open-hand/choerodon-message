package io.choerodon.message.api.vo;

import java.util.List;
import javax.persistence.Column;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.message.domain.entity.MessageTemplate;
import org.hzero.message.domain.entity.TemplateServer;

/**
 * @author jiameng.cao
 * @date 2019/10/30
 */
public class SendSettingVO extends TemplateServer {

    @ApiModelProperty(value = "邮件重发次数")
    private Integer retryCount;

    private Integer emailEnabledFlag;

    private Integer pmEnabledFlag;

    private Integer smsEnabledFlag;

    private Integer webhookEnabledFlag;

    private Integer webhookJsonEnabledFlag;

    private List<MessageTemplate> messageTemplates;

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

    public List<MessageTemplate> getMessageTemplates() {
        return messageTemplates;
    }

    public void setMessageTemplates(List<MessageTemplate> messageTemplates) {
        this.messageTemplates = messageTemplates;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }
}
