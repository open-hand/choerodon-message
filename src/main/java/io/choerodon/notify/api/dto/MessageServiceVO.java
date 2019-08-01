package io.choerodon.notify.api.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * 该VO用于消息列表页(对应库表 notify_send_setting)
 *
 * @author Eugen
 */
public class MessageServiceVO {

    @ApiModelProperty(value = "发送设置主键")
    private Long id;

    // 对应notify_send_setting.name
    @ApiModelProperty(value = "消息类型")
    private String messageType;

    // 对应notify_send_setting.description
    @ApiModelProperty(value = "说明")
    private String introduce;

    @ApiModelProperty(value = "层级")
    private String level;

    // 对应notify_send_setting.is_enabled
    @ApiModelProperty(value = "启用状态")
    private Boolean enabled;

    // 对应notify_send_setting.is_allow_config
    @ApiModelProperty(value = "是否允许配置接收")
    private Boolean allowConfig;

    @ApiModelProperty(value = "乐观所版本号")
    private Long objectVersionNumber;

    public Long getId() {
        return id;
    }

    public MessageServiceVO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getMessageType() {
        return messageType;
    }

    public MessageServiceVO setMessageType(String messageType) {
        this.messageType = messageType;
        return this;
    }

    public String getIntroduce() {
        return introduce;
    }

    public MessageServiceVO setIntroduce(String introduce) {
        this.introduce = introduce;
        return this;
    }

    public String getLevel() {
        return level;
    }

    public MessageServiceVO setLevel(String level) {
        this.level = level;
        return this;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public MessageServiceVO setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public Boolean getAllowConfig() {
        return allowConfig;
    }

    public MessageServiceVO setAllowConfig(Boolean allowConfig) {
        this.allowConfig = allowConfig;
        return this;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public MessageServiceVO setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
        return this;
    }
}
