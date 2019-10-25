package io.choerodon.notify.infra.dto;

import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author jiameng.cao
 * @date 2019/10/25
 */
@Table(name = "notify_webhook_message_setting")
public class WebHookMessageSettingDTO extends BaseDTO {
    @Id
    @GeneratedValue
    private Long id;

    private Long webhookId;

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

    public void setWebhookId(Long webhookId) {
        this.webhookId = webhookId;
    }

    public Long getSendSettingId() {
        return sendSettingId;
    }

    public void setSendSettingId(Long sendSettingId) {
        this.sendSettingId = sendSettingId;
    }
}
