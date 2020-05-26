package io.choerodon.message.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * 该VO用于消息列表页(对应库表 notify_send_setting)
 *
 * @author Eugen
 */
public class MessageServiceVO {

    @ApiModelProperty(value = "发送设置主键")
    private Long id;

    @ApiModelProperty(value = "模版编码")
    private String messageCode;

    @ApiModelProperty(value = "消息名称")
    private String messageName;

    // 对应notify_send_setting.name
    @ApiModelProperty(value = "消息类型")
    private String messageType;

    @ApiModelProperty(value = "消息类型Value")
    private String messageTypeValue;

    @ApiModelProperty(value = "层级")
    private String level;

    // 对应notify_send_setting.is_enabled
    @ApiModelProperty(value = "启用状态")
    private Boolean enabled;

    // 对应notify_send_setting.is_allow_config
    @ApiModelProperty(value = "是否允许配置接收")
    private Boolean receiveConfigFlag;

    @ApiModelProperty(value = "是否允许更改，允许配置接收")
    private Boolean edit;

    @ApiModelProperty(value = "乐观所版本号")
    private Long objectVersionNumber;

    @ApiModelProperty(value = "描述字段")
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public MessageServiceVO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getMessageTypeValue() {
        return messageTypeValue;
    }

    public void setMessageTypeValue(String messageTypeValue) {
        this.messageTypeValue = messageTypeValue;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    public Boolean getReceiveConfigFlag() {
        return receiveConfigFlag;
    }

    public void setReceiveConfigFlag(Boolean receiveConfigFlag) {
        this.receiveConfigFlag = receiveConfigFlag;
    }

    public String getMessageType() {
        return messageType;
    }

    public MessageServiceVO setMessageType(String messageType) {
        this.messageType = messageType;
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

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public MessageServiceVO setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
        return this;
    }

    public Boolean getEdit() {
        return edit;
    }

    public void setEdit(Boolean edit) {
        this.edit = edit;
    }

    public String getMessageName() {
        return messageName;
    }

    public void setMessageName(String messageName) {
        this.messageName = messageName;
    }
}
