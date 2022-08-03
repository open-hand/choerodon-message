package io.choerodon.message.api.vo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.Transient;

/**
 * 〈功能简述〉
 * 〈自定义通知设置VO〉
 *
 * @author wanghao
 * @Date 2019/12/11 11:16
 */
public class CustomMessageSettingVO {
    @Encrypt
    private Long id;

    @ApiModelProperty("资源Id")
    private Long sourceId;

    @Transient
    private Long projectId;

    @ApiModelProperty("资源层级")
    private String sourceLevel;

    @Encrypt
    @ApiModelProperty("环境Id")
    private Long envId;

    @ApiModelProperty("通知类型，用作tab分页，敏捷消息类型agileNotify，devops消息类型devopsNotify")
    private String notifyType;

    @ApiModelProperty(value = "分组的id,这就为categoryCode")
    private String groupId;

    @ApiModelProperty("消息code")
    private String code;

    @ApiModelProperty(value = "消息设置的名字")
    private String name;

    @ApiModelProperty("是否发送站内信")
    private Boolean pmEnable;

    @ApiModelProperty("平台层配置是否允许发送站内信")
    private Boolean pmEnabledFlag = false;

    @ApiModelProperty("是否发送邮件")
    private Boolean emailEnable;

    @ApiModelProperty("平台层配置是否允许发送邮件")
    private Boolean emailEnabledFlag = false;

    @ApiModelProperty("是否发送短信")
    private Boolean smsEnable;

    @ApiModelProperty("是否发送钉钉")
    private Boolean dtEnable;

    @ApiModelProperty("平台层配置是否允许发送钉钉")
    private Boolean dtEnabledFlag = false;

    @ApiModelProperty("平台层配置是否允许发送短信")
    private Boolean smsEnabledFlag = false;

    @ApiModelProperty(value = "版本号")
    private Long objectVersionNumber;

    @ApiModelProperty(value = "资源删除验证事件名字")
    private String eventName;

    @ApiModelProperty(value = "通知对象")
    private String notifyObject;

    @ApiModelProperty(value = "通知的非指定用户集合")
    private Set<String> sendRoleList = new HashSet<>();

    @ApiModelProperty(value = "指定用户集合")
    private List<TargetUserVO> userList;

    @Encrypt
    @ApiModelProperty(value = "指定用户id集合")
    private Set<Long> specifierIds;

    @JsonIgnore
    private SendSettingVO sendSetting;

    @ApiModelProperty(value = "消息父类型code")
    private String subcategoryCode;

    @ApiModelProperty("消息对应的line表中的类型是否启用")
    private Boolean enabledFlag;

    private String sendingType;

    public String getSendingType() {
        return sendingType;
    }

    public void setSendingType(String sendingType) {
        this.sendingType = sendingType;
    }

    public Boolean getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Boolean enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getSubcategoryCode() {
        return subcategoryCode;
    }

    public void setSubcategoryCode(String subcategoryCode) {
        this.subcategoryCode = subcategoryCode;
    }

    private int order;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceLevel() {
        return sourceLevel;
    }

    public void setSourceLevel(String sourceLevel) {
        this.sourceLevel = sourceLevel;
    }

    public String getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(String notifyType) {
        this.notifyType = notifyType;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Boolean getSmsEnable() {
        return smsEnable;
    }

    public void setSmsEnable(Boolean smsEnable) {
        this.smsEnable = smsEnable;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public List<TargetUserVO> getUserList() {
        return userList;
    }

    public void setUserList(List<TargetUserVO> userList) {
        this.userList = userList;
    }

    public Long getEnvId() {
        return envId;
    }

    public void setEnvId(Long envId) {
        this.envId = envId;
    }

    public Set<String> getSendRoleList() {
        return sendRoleList;
    }

    public void setSendRoleList(Set<String> sendRoleList) {
        this.sendRoleList = sendRoleList;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getNotifyObject() {
        return notifyObject;
    }

    public void setNotifyObject(String notifyObject) {
        this.notifyObject = notifyObject;
    }

    public Set<Long> getSpecifierIds() {
        return specifierIds;
    }

    public void setSpecifierIds(Set<Long> specifierIds) {
        this.specifierIds = specifierIds;
    }

    public SendSettingVO getSendSetting() {
        return sendSetting;
    }

    public void setSendSetting(SendSettingVO sendSetting) {
        this.sendSetting = sendSetting;
    }

    public Boolean getPmEnabledFlag() {
        return pmEnabledFlag;
    }

    public void setPmEnabledFlag(Boolean pmEnabledFlag) {
        this.pmEnabledFlag = pmEnabledFlag;
    }

    public Boolean getEmailEnabledFlag() {
        return emailEnabledFlag;
    }

    public void setEmailEnabledFlag(Boolean emailEnabledFlag) {
        this.emailEnabledFlag = emailEnabledFlag;
    }

    public Boolean getSmsEnabledFlag() {
        return smsEnabledFlag;
    }

    public void setSmsEnabledFlag(Boolean smsEnabledFlag) {
        this.smsEnabledFlag = smsEnabledFlag;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Boolean getDtEnable() {
        return dtEnable;
    }

    public void setDtEnable(Boolean dtEnable) {
        this.dtEnable = dtEnable;
    }

    public Boolean getDtEnabledFlag() {
        return dtEnabledFlag;
    }

    public void setDtEnabledFlag(Boolean dtEnabledFlag) {
        this.dtEnabledFlag = dtEnabledFlag;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}

