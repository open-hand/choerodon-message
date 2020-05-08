package io.choerodon.message.infra.dto;

import javax.persistence.Table;

import io.swagger.annotations.ApiModelProperty;

import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * @author scp
 * @date 2020/5/9
 * @description
 */
@Table(name = "message_template_rel")
public class MessageTemplateRelDTO extends AuditDomain {
    private Long id;

    @ApiModelProperty(value = "消息code")
    private String messageCode;

    @ApiModelProperty(value = "消息模板Id")
    private Long templateId;

    @ApiModelProperty(value = "是否启用")
    private Integer enabledFlag;

    @ApiModelProperty(value = "发送类型")
    private String sendType;

    public MessageTemplateRelDTO() {
    }

    public MessageTemplateRelDTO(String messageCode) {
        this.messageCode = messageCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
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

    public String getSendType() {
        return sendType;
    }

    public void setSendType(String sendType) {
        this.sendType = sendType;
    }
}
