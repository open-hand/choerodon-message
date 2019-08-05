package io.choerodon.notify.api.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

/**
 * 该VO用于各模版内容页的列表显示
 * 邮件/站内信/短信
 * 对应库表 notify_template
 *
 * @author Eugen
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TemplateVO {

    @ApiModelProperty(value = "模版主键")
    private Long id;

    @ApiModelProperty(value = "模版名称")
    private String name;

    @ApiModelProperty(value = "是否为预定义")
    private Boolean predefined;

    @ApiModelProperty(value = "邮件标题")
    private String emailTitle;

    @ApiModelProperty(value = "邮件内容")
    private String emailContent;

    @ApiModelProperty(value = "站内信标题")
    private String pmTitle;

    @ApiModelProperty(value = "站内信内容")
    private String pmContent;

    @ApiModelProperty(value = "短信内容")
    private String smsContent;

    @ApiModelProperty(value = "乐观锁版本号")
    private Long objectVersionNumber;

    public Long getId() {
        return id;
    }

    public TemplateVO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public TemplateVO setName(String name) {
        this.name = name;
        return this;
    }

    public Boolean getPredefined() {
        return predefined;
    }

    public TemplateVO setPredefined(Boolean predefined) {
        this.predefined = predefined;
        return this;
    }

    public String getEmailTitle() {
        return emailTitle;
    }

    public TemplateVO setEmailTitle(String emailTitle) {
        this.emailTitle = emailTitle;
        return this;
    }

    public String getPmTitle() {
        return pmTitle;
    }

    public TemplateVO setPmTitle(String pmTitle) {
        this.pmTitle = pmTitle;
        return this;
    }

    public String getSmsContent() {
        return smsContent;
    }

    public TemplateVO setSmsContent(String smsContent) {
        this.smsContent = smsContent;
        return this;
    }

    public String getEmailContent() {
        return emailContent;
    }

    public TemplateVO setEmailContent(String emailContent) {
        this.emailContent = emailContent;
        return this;
    }

    public String getPmContent() {
        return pmContent;
    }

    public TemplateVO setPmContent(String pmContent) {
        this.pmContent = pmContent;
        return this;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public TemplateVO setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
        return this;
    }
}
