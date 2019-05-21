package io.choerodon.notify.api.dto;

import io.choerodon.notify.domain.ReceiveSetting;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;
import org.modelmapper.PropertyMap;

import javax.validation.constraints.NotNull;

/**
 * @author dengyouquan
 **/
public class ReceiveSettingDTO {
    private Long id;
    @ApiModelProperty(value = "发送设置id/必填")
    @NotNull(message = "error.receiveSetting.sendSettingIdNull")
    private Long sendSettingId;
    @ApiModelProperty(value = "消息类型（email,pm）/必填")
    @NotEmpty(message = "error.receiveSetting.messageTypeEmpty")
    private String messageType;
    @ApiModelProperty(value = "是否禁用消息通知/非必填")
    private Boolean disable;
    private Long sourceId;
    private String sourceType;
    @ApiModelProperty(value = "用户id/必填")
    @NotNull(message = "error.receiveSetting.userIdNull")
    private Long userId;

    public static PropertyMap<ReceiveSettingDTO, ReceiveSetting> dto2Entity() {
        return new PropertyMap<ReceiveSettingDTO, ReceiveSetting>() {
            @Override
            protected void configure() {
                skip().setCreatedBy(null);
                skip().setCreationDate(null);
                skip().setLastUpdateDate(null);
                skip().setLastUpdatedBy(null);
                skip().setObjectVersionNumber(null);

                skip().set__id(null);
                skip().set__tls(null);
                skip().set__status(null);
                skip().setSortname(null);
                skip().setSortorder(null);
                skip().set_token(null);
                skip().setRequestId(null);
                skip().setProgramId(null);
                skip().setAttributeCategory(null);
                skip().setAttribute1(null);
                skip().setAttribute2(null);
                skip().setAttribute3(null);
                skip().setAttribute4(null);
                skip().setAttribute5(null);
                skip().setAttribute6(null);
                skip().setAttribute7(null);
                skip().setAttribute8(null);
                skip().setAttribute9(null);
                skip().setAttribute10(null);
                skip().setAttribute11(null);
                skip().setAttribute12(null);
                skip().setAttribute13(null);
                skip().setAttribute14(null);
                skip().setAttribute15(null);
            }
        };
    }


    public static PropertyMap<ReceiveSetting, ReceiveSettingDTO> entity2Dto() {
        return new PropertyMap<ReceiveSetting, ReceiveSettingDTO>() {
            protected void configure() {
                //因为ReceiveSetting 和 ReceiveSettingDTO 字段完全相同，我们使用默认转换
            }
        };
    }

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

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
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
