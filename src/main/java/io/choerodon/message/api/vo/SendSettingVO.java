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

    // todo 等待hzero添加
//    private Integer retryCount;

    private Boolean emailEnabledFlag;

    private Boolean pmEnabledFlag;

    private Boolean smsEnabledFlag;

    private Boolean webhookEnabledFlag;

    private Boolean webhookJsonEnabledFlag;

    private List<MessageTemplate> messageTemplates;

    public Boolean getEmailEnabledFlag() {
        return emailEnabledFlag;
    }

    public void setEmailEnabledFlag(Boolean emailEnabledFlag) {
        this.emailEnabledFlag = emailEnabledFlag;
    }

    public Boolean getPmEnabledFlag() {
        return pmEnabledFlag;
    }

    public void setPmEnabledFlag(Boolean pmEnabledFlag) {
        this.pmEnabledFlag = pmEnabledFlag;
    }

    public Boolean getSmsEnabledFlag() {
        return smsEnabledFlag;
    }

    public void setSmsEnabledFlag(Boolean smsEnabledFlag) {
        this.smsEnabledFlag = smsEnabledFlag;
    }

    public Boolean getWebhookEnabledFlag() {
        return webhookEnabledFlag;
    }

    public void setWebhookEnabledFlag(Boolean webhookEnabledFlag) {
        this.webhookEnabledFlag = webhookEnabledFlag;
    }

    public Boolean getWebhookJsonEnabledFlag() {
        return webhookJsonEnabledFlag;
    }

    public void setWebhookJsonEnabledFlag(Boolean webhookJsonEnabledFlag) {
        this.webhookJsonEnabledFlag = webhookJsonEnabledFlag;
    }

    public List<MessageTemplate> getMessageTemplates() {
        return messageTemplates;
    }

    public void setMessageTemplates(List<MessageTemplate> messageTemplates) {
        this.messageTemplates = messageTemplates;
    }
}
