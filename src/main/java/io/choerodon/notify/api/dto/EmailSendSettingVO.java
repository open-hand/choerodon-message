package io.choerodon.notify.api.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * 该VO用于邮件内容设置的发送设置(对应库表 notify_send_setting {@link io.choerodon.notify.domain.SendSetting})
 *
 * @author Eugen
 */
public class EmailSendSettingVO {

    @ApiModelProperty(value = "发送设置主键")
    private Long id;

    @ApiModelProperty(value = "邮件模版主键")
    private Long emailTemplateId;

    @ApiModelProperty(value = "邮件模版名称")
    private String emailTemplateTitle;

    @ApiModelProperty(value = "重试次数")
    private Integer retryCount;

    @ApiModelProperty(value = "是否即时发送")
    private Boolean sendInstantly;

    @ApiModelProperty(value = "是否可手动重试")
    private Boolean manualRetry;

    @ApiModelProperty(value = "乐观所版本号")
    private Long objectVersionNumber;

    public Long getId() {
        return id;
    }

    public EmailSendSettingVO setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getEmailTemplateId() {
        return emailTemplateId;
    }

    public EmailSendSettingVO setEmailTemplateId(Long emailTemplateId) {
        this.emailTemplateId = emailTemplateId;
        return this;
    }

    public String getEmailTemplateTitle() {
        return emailTemplateTitle;
    }

    public EmailSendSettingVO setEmailTemplateTitle(String emailTemplateTitle) {
        this.emailTemplateTitle = emailTemplateTitle;
        return this;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public EmailSendSettingVO setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
        return this;
    }

    public Boolean getSendInstantly() {
        return sendInstantly;
    }

    public EmailSendSettingVO setSendInstantly(Boolean sendInstantly) {
        this.sendInstantly = sendInstantly;
        return this;
    }

    public Boolean getManualRetry() {
        return manualRetry;
    }

    public EmailSendSettingVO setManualRetry(Boolean manualRetry) {
        this.manualRetry = manualRetry;
        return this;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public EmailSendSettingVO setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
        return this;
    }
}
