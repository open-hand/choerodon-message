package io.choerodon.message.api.vo;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.message.domain.entity.MessageTemplate;

/**
 * @author scp
 * @date 2020/5/23
 * @description
 */
public class MessageTemplateVO extends MessageTemplate {
    @ApiModelProperty(value = "发送类型")
    private String sendingType;

    @ApiModelProperty(value = "发送类型, Json/DingTalkAndWeChat")
    private String webhookType;

    @ApiModelProperty(value = "消息code")
    private String messageCode;

    public String getWebhookType() {
        return webhookType;
    }

    public void setWebhookType(String webhookType) {
        this.webhookType = webhookType;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    public String getSendingType() {
        return sendingType;
    }

    public void setSendingType(String sendingType) {
        this.sendingType = sendingType;
    }
}
