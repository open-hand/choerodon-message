package io.choerodon.notify.api.dto;

import io.choerodon.notify.infra.dto.TargetUserDTO;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * User: Mr.Wang
 * Date: 2019/12/3
 */
public class MessageSettingVO {
    private Long id;
    @NotEmpty(message = "error.messageSetting.notifyType.empty")
    @ApiModelProperty("通知类型（必填），用作tab分页，敏捷消息类型agileNotify，devops消息类型devopsNotify")
    private String notifyType;
    @ApiModelProperty("消息code")
    private String code;
    @ApiModelProperty("项目Id")
    private Long projectId;
    @ApiModelProperty("是否发送站内信")
    private Boolean pmEnable;
    @ApiModelProperty("是否发送邮件")
    private Boolean emailEnable;
    @ApiModelProperty(value = "版本号")
    private Long objectVersionNumber;
    @ApiModelProperty(value = "消息设置的分组")
    private String category;
    @ApiModelProperty(value = "分组的id")
    private Long categoryId;
    @ApiModelProperty(value = "消息接收对象")
    private List<TargetUserDTO> targetUserDTOS;
    @ApiModelProperty(value = "消息设置的名字")
    private String name;
    @ApiModelProperty(value = "环境的id")
    private Long envId;
    @ApiModelProperty(value = "是否发送短信")
    private Boolean smsEnable;

    public Long getEnvId() {
        return envId;
    }

    public void setEnvId(Long envId) {
        this.envId = envId;
    }

    public Boolean getSmsEnable() {
        return smsEnable;
    }

    public void setSmsEnable(Boolean smsEnable) {
        this.smsEnable = smsEnable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<TargetUserDTO> getTargetUserDTOS() {
        return targetUserDTOS;
    }

    public void setTargetUserDTOS(List<TargetUserDTO> targetUserDTOS) {
        this.targetUserDTOS = targetUserDTOS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(String notifyType) {
        this.notifyType = notifyType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

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

    @Override
    public String toString() {
        return "MessageSettingVO{" +
                "id=" + id +
                ", notifyType='" + notifyType + '\'' +
                ", code='" + code + '\'' +
                ", projectId=" + projectId +
                ", pmEnable=" + pmEnable +
                ", emailEnable=" + emailEnable +
                ", objectVersionNumber=" + objectVersionNumber +
                ", category='" + category + '\'' +
                ", categoryId=" + categoryId +
                ", targetUserDTOS=" + targetUserDTOS +
                ", name='" + name + '\'' +
                '}';
    }
}
