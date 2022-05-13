package io.choerodon.message.api.vo;

import java.util.Set;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author superlee
 * @since 2021-03-04
 */
public class ProjectMessageVO extends ProjectVO {

    private Set<Long> userIds;

    private Set<String> receiverTypes;

    @ApiModelProperty("是否发送站内信")
    private Boolean pmEnable;

    @ApiModelProperty("是否发送邮件")
    private Boolean emailEnable;

    @ApiModelProperty("是否发送短信")
    private Boolean smsEnable;
    @ApiModelProperty("是否发送钉钉")
    private Boolean dtEnable;

    public Boolean getPmEnable() {
        return pmEnable;
    }

    public void setPmEnable(Boolean pmEnable) {
        this.pmEnable = pmEnable;
    }

    public Boolean getEmailEnable() {
        return emailEnable;
    }

    public void setEmailEnable(Boolean emailEnable) {
        this.emailEnable = emailEnable;
    }

    public Boolean getSmsEnable() {
        return smsEnable;
    }

    public void setSmsEnable(Boolean smsEnable) {
        this.smsEnable = smsEnable;
    }

    public Set<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(Set<Long> userIds) {
        this.userIds = userIds;
    }

    public Set<String> getReceiverTypes() {
        return receiverTypes;
    }

    public void setReceiverTypes(Set<String> receiverTypes) {
        this.receiverTypes = receiverTypes;
    }

    public Boolean getDtEnable() {
        return dtEnable;
    }

    public void setDtEnable(Boolean dtEnable) {
        this.dtEnable = dtEnable;
    }
}
