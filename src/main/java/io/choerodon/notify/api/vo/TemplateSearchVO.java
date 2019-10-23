package io.choerodon.notify.api.vo;

/**
 * 消息模板查询VO.
 */
public class TemplateSearchVO {
    private String businessType;
    private String messageType;
    private String name;
    private Boolean predefined;
    private String[] params;

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getPredefined() {
        return predefined;
    }

    public void setPredefined(Boolean predefined) {
        this.predefined = predefined;
    }

    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }
}
