package io.choerodon.notify.infra.dto;

import io.choerodon.mybatis.entity.BaseDTO;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;

/**
 * User: Mr.Wang
 * Date: 2019/12/3
 */
@Table(name = "notify_message_setting")
public class MessageSettingDTO extends BaseDTO {
    @Id
    @GeneratedValue
    private Long id;
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
    private transient String category;
    @ApiModelProperty(value = "消息接收对象")
    private TargetUserDTO targetUserDTO;
    @ApiModelProperty(value = "消息设置的名字")
    private transient String name;


    @Override
    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    @Override
    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public TargetUserDTO getTargetUserDTO() {
        return targetUserDTO;
    }

    public void setTargetUserDTO(TargetUserDTO targetUserDTO) {
        this.targetUserDTO = targetUserDTO;
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
        return "MessageSettingDTO{" +
                "id=" + id +
                ", notifyType='" + notifyType + '\'' +
                ", code='" + code + '\'' +
                ", projectId=" + projectId +
                ", pmEnable=" + pmEnable +
                ", emailEnable=" + emailEnable +
                ", objectVersionNumber=" + objectVersionNumber +
                ", category='" + category + '\'' +
                ", targetUserDTO=" + targetUserDTO +
                ", name='" + name + '\'' +
                '}';
    }
}
