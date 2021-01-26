package io.choerodon.message.api.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import io.choerodon.mybatis.domain.AuditDomain;

/**
 * @author scp
 * @date 2020/5/23
 * @description
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageTemplateVO extends AuditDomain {
    @ApiModelProperty(value = "发送类型")
    private String sendingType;

    @ApiModelProperty(value = "发送类型, Json/DingTalkAndWeChat")
    private String webhookType;

    @ApiModelProperty(value = "消息code")
    private String messageCode;

    @ApiModelProperty("消息模板ID")
    @Encrypt
    private Long templateId;
    private Long tenantId;
    @ApiModelProperty("模板编码")
    private String templateCode;
    @ApiModelProperty("模板名称")
    private String templateName;
    @ApiModelProperty("模板标题")
    private String templateTitle;
    @ApiModelProperty("模板内容")
    private String templateContent;
    @ApiModelProperty(value = "消息类型，值集:HMSG.MESSAGE_CATEGORY")
    private String messageCategoryCode;
    @ApiModelProperty(value = "消息子类型，值集:HMSG.MESSAGE_SUBCATEGORY")
    private String messageSubcategoryCode;
    @ApiModelProperty("短信非空，外部代码")
    private String externalCode;
    @ApiModelProperty(value = "取值SQL")
    private String sqlValue;
    @ApiModelProperty(value = "语言编码")
    private String lang;
    @ApiModelProperty(value = "启用标识", allowableValues = "range[0, 1]")
    private Integer enabledFlag;
    @ApiModelProperty(value = "编辑器类型，值集HMSG.TEMPLATE_EDITOR_TYPE")
    private String editorType;

    public String getSendingType() {
        return sendingType;
    }

    public void setSendingType(String sendingType) {
        this.sendingType = sendingType;
    }

    public String getWebhookType() {
        return webhookType;
    }

    public void setWebhookType(String webhookType) {
        this.webhookType = webhookType;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateTitle() {
        return templateTitle;
    }

    public void setTemplateTitle(String templateTitle) {
        this.templateTitle = templateTitle;
    }

    public String getTemplateContent() {
        return templateContent;
    }

    public void setTemplateContent(String templateContent) {
        this.templateContent = templateContent;
    }

    public String getMessageCategoryCode() {
        return messageCategoryCode;
    }

    public void setMessageCategoryCode(String messageCategoryCode) {
        this.messageCategoryCode = messageCategoryCode;
    }

    public String getMessageSubcategoryCode() {
        return messageSubcategoryCode;
    }

    public void setMessageSubcategoryCode(String messageSubcategoryCode) {
        this.messageSubcategoryCode = messageSubcategoryCode;
    }

    public String getExternalCode() {
        return externalCode;
    }

    public void setExternalCode(String externalCode) {
        this.externalCode = externalCode;
    }

    public String getSqlValue() {
        return sqlValue;
    }

    public void setSqlValue(String sqlValue) {
        this.sqlValue = sqlValue;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getEditorType() {
        return editorType;
    }

    public void setEditorType(String editorType) {
        this.editorType = editorType;
    }
}
