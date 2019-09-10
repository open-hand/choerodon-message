package io.choerodon.notify.api.vo;

/**
 * 消息服务查询VO.
 *
 * @author wkj
 * @since 2019/9/9
 */
public class MessageServiceSearchVO {
    private String messageType;
    private String introduce;
    private String level;
    private Boolean enabled;
    private Boolean allowConfig;
    private String[] params;

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getAllowConfig() {
        return allowConfig;
    }

    public void setAllowConfig(Boolean allowConfig) {
        this.allowConfig = allowConfig;
    }

    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }
}
