package io.choerodon.message.api.vo;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.modelmapper.PropertyMap;

import io.choerodon.message.infra.dto.ReceiveSettingDTO;

/**
 * @author dengyouquan
 **/
public class ReceiveSettingVO {
    @Encrypt
    private Long id;

    @Encrypt
    @ApiModelProperty(value = "发送设置id/必填")
    @NotNull(message = "error.receiveSetting.sendSettingIdNull")
    private Long sendSettingId;

    @ApiModelProperty(value = "消息类型（email,web）/必填")
    @NotEmpty(message = "error.receiveSetting.sendingTypeEmpty")
    private String sendingType;
    @ApiModelProperty(value = "是否禁用消息通知/非必填")
    private Boolean disable;

    private Long sourceId;
    private String sourceType;

    @Encrypt
    @ApiModelProperty(value = "用户id/必填")
    @NotNull(message = "error.receiveSetting.userIdNull")
    private Long userId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSendSettingId() {
        return sendSettingId;
    }

    public void setSendSettingId(Long sendSettingId) {
        this.sendSettingId = sendSettingId;
    }

    public String getSendingType() {
        return sendingType;
    }

    public void setSendingType(String sendingType) {
        this.sendingType = sendingType;
    }

    public Boolean getDisable() {
        return disable;
    }

    public void setDisable(Boolean disable) {
        this.disable = disable;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
