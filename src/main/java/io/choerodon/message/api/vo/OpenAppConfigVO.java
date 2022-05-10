package io.choerodon.message.api.vo;

import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import java.util.Map;

import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * Created by wangxiang on 2022/3/24
 */
public class OpenAppConfigVO {

    @Encrypt
    private Long id;

    @Encrypt
    @ApiModelProperty("oauth_open_app 主键")
    private Long openAppId;

    @ApiModelProperty("登录名映射字段")
    private String loginNameField;

    @ApiModelProperty("真实姓名映射字段")
    private String realNameField;

    @ApiModelProperty("邮箱号码映射字段")
    private String emailField;

    @ApiModelProperty("手机号码映射字段")
    private String phoneField;

    @ApiModelProperty("cron表达式")
    private String cronExpression;

    @ApiModelProperty("是否启用定时同步")
    private Boolean timingFlag;

    @ApiModelProperty("开始同步时间")
    private Date startSyncTime;

    @ApiModelProperty("同步频率")
    private String frequency;

    @ApiModelProperty("是否启用同步工作项")
    private Boolean agileFlag;

    @ApiModelProperty("是否启用同步通讯录架构到工作组")
    private Boolean workGroupFlag;
    @ApiModelProperty("是否同步用户")
    private Boolean userFlag;
    @ApiModelProperty("是否开启消息发送")
    private Boolean messageFlag;

    @ApiModelProperty("工作项优先级映射字段")
    @Encrypt
    private Map<String,String> agilePriorityMap;

    @ApiModelProperty("定时任务Id")
    @Encrypt
    private Long quartzTaskId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOpenAppId() {
        return openAppId;
    }

    public void setOpenAppId(Long openAppId) {
        this.openAppId = openAppId;
    }

    public String getLoginNameField() {
        return loginNameField;
    }

    public void setLoginNameField(String loginNameField) {
        this.loginNameField = loginNameField;
    }

    public String getRealNameField() {
        return realNameField;
    }

    public void setRealNameField(String realNameField) {
        this.realNameField = realNameField;
    }

    public String getEmailField() {
        return emailField;
    }

    public void setEmailField(String emailField) {
        this.emailField = emailField;
    }

    public String getPhoneField() {
        return phoneField;
    }

    public void setPhoneField(String phoneField) {
        this.phoneField = phoneField;
    }

    public Boolean getTimingFlag() {
        return timingFlag;
    }

    public void setTimingFlag(Boolean timingFlag) {
        this.timingFlag = timingFlag;
    }

    public Date getStartSyncTime() {
        return startSyncTime;
    }

    public void setStartSyncTime(Date startSyncTime) {
        this.startSyncTime = startSyncTime;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public Long getQuartzTaskId() {
        return quartzTaskId;
    }

    public void setQuartzTaskId(Long quartzTaskId) {
        this.quartzTaskId = quartzTaskId;
    }

    public Boolean getAgileFlag() {
        return agileFlag;
    }

    public void setAgileFlag(Boolean agileFlag) {
        this.agileFlag = agileFlag;
    }

    public Map<String, String> getAgilePriorityMap() {
        return agilePriorityMap;
    }

    public void setAgilePriorityMap(Map<String, String> agilePriorityMap) {
        this.agilePriorityMap = agilePriorityMap;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public Boolean getWorkGroupFlag() {
        return workGroupFlag;
    }

    public void setWorkGroupFlag(Boolean workGroupFlag) {
        this.workGroupFlag = workGroupFlag;
    }

    public Boolean getUserFlag() {
        return userFlag;
    }

    public void setUserFlag(Boolean userFlag) {
        this.userFlag = userFlag;
    }

    public Boolean getMessageFlag() {
        return messageFlag;
    }

    public void setMessageFlag(Boolean messageFlag) {
        this.messageFlag = messageFlag;
    }
}



