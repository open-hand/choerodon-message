package io.choerodon.message.infra.dto;

import java.util.List;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * User: Mr.Wang
 * Date: 2019/12/3
 */

@VersionAudit
@ModifyAudit
@Table(name = "notify_message_setting")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageSettingDTO extends AuditDomain {

    @Id
    @GeneratedValue
    private Long id;
    @ApiModelProperty("通知类型（必填），用作tab分页，敏捷消息类型agile，devops消息类型devops")
    private String notifyType;
    @ApiModelProperty("消息code")
    private String code;
    @ApiModelProperty("项目Id")
    private Long projectId;
    @ApiModelProperty("是否发送站内信")
    private Boolean pmEnable;
    @ApiModelProperty("是否发送邮件")
    private Boolean emailEnable;

    @ApiModelProperty(value = "消息设置的分组")
    @Transient
    private  String category;

    @ApiModelProperty(value = "消息接收对象")
    @Transient
    private  List<TargetUserDTO> targetUserDTOS;

    @ApiModelProperty(value = "消息设置的名字")
    @Transient
    private  String name;

    @ApiModelProperty(value = "分组的id")
    @Transient
    private  Long categoryId;

    @ApiModelProperty(value = "环境的id")
    private Long envId;
    @ApiModelProperty(value = "是否发送短信")
    private Boolean smsEnable;
    @ApiModelProperty(value = "资源删除验证事件名字")
    private String eventName;
    @ApiModelProperty(value = "是否发送钉钉")
    private Boolean dtEnable;

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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
        return "MessageSettingDTO{" +
                "id=" + id +
                ", notifyType='" + notifyType + '\'' +
                ", code='" + code + '\'' +
                ", projectId=" + projectId +
                ", pmEnable=" + pmEnable +
                ", emailEnable=" + emailEnable +
                ", objectVersionNumber=" + super.getObjectVersionNumber() +
                ", category='" + category + '\'' +
                ", categoryId=" + categoryId +
                ", targetUserDTOS=" + targetUserDTOS +
                ", name='" + name + '\'' +
                '}';
    }

    public Boolean getDtEnable() {
        return dtEnable;
    }

    public void setDtEnable(Boolean dtEnable) {
        this.dtEnable = dtEnable;
    }
}
