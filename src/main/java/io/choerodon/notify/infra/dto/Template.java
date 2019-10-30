package io.choerodon.notify.infra.dto;



import io.choerodon.mybatis.entity.BaseDTO;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 消息模板实体
 * 包括邮件,站内信模板和短信模版
 */
@Table(name = "notify_template")
public class Template extends BaseDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "模板id/非必填")
    private Long id;

    @ApiModelProperty(value = "模板是否为预定义/必填")
    @NotNull(message = "error.template.isPredefined.null")
    private Boolean isPredefined;

    @ApiModelProperty(value = "模版类型:email,sms,pm,webhook/必填")
    @NotNull(message = "error.template.sendingType.null")
    private String sendingType;

    @ApiModelProperty(value = "模版业务类型/必填")
    @NotNull(message = "error.template.sendSettingCode.null")
    private String sendSettingCode;

    @ApiModelProperty(value = "模版标题/非必填")
    private String title;

    @ApiModelProperty(value = "模版内容/必填")
    @NotEmpty(message = "error.template.content.empty")
    private String content;



    public Template() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getIsPredefined() {
        return isPredefined;
    }

    public void setIsPredefined(Boolean predefined) {
        isPredefined = predefined;
    }

    public String getSendingType() {
        return sendingType;
    }

    public Template setSendingType(String sendingType) {
        this.sendingType = sendingType;
        return this;
    }

    public String getSendSettingCode() {
        return sendSettingCode;
    }

    public Template setSendSettingCode(String sendSettingCode) {
        this.sendSettingCode = sendSettingCode;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
