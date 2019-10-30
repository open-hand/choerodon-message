package io.choerodon.notify.infra.dto;

import io.choerodon.mybatis.entity.BaseDTO;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author jiameng.cao
 * @date 2019/10/25
 */
@Table(name = "notify_webhook_message_setting")
public class WebHookMessageSettingDTO extends BaseDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("必填字段")
    private Long id;

    @ApiModelProperty("WEBHOOK主键")
    private Long webhookId;

    @ApiModelProperty("发送设置主键")
    private Long sendSettingId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWebhookId() {
        return webhookId;
    }

    public WebHookMessageSettingDTO setWebhookId(Long webhookId) {
        this.webhookId = webhookId;
        return this;
    }

    public Long getSendSettingId() {
        return sendSettingId;
    }

    public WebHookMessageSettingDTO setSendSettingId(Long sendSettingId) {
        this.sendSettingId = sendSettingId;
        return this;
    }
}
