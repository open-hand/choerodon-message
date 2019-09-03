package io.choerodon.notify.api.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.notify.api.validator.Insert;
import io.choerodon.notify.api.validator.Update;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 该VO用于各模版内容页的模版创建/修改
 * 邮件/站内信/短信
 * 对应库表 notify_template
 *
 * @author Eugen
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TemplateCreateVO {
    @ApiModelProperty(value = "模版主键")
    private Long id;

    @NotEmpty(message = "error.template.create.name.cannot.be.empty", groups = {Insert.class})
    @ApiModelProperty(value = "模版名称")
    private String name;

    @NotEmpty(message = "error.template.create.code.cannot.be.empty", groups = {Insert.class})
    @ApiModelProperty(value = "模版编码")
    private String code;

    @NotEmpty(message = "error.template.create.business.type.cannot.be.empty", groups = {Insert.class, Update.class})
    @ApiModelProperty(value = "模版业务类型")
    private String businessType;

    @ApiModelProperty(value = "是否为预定义")
    private Boolean predefined;

    @NotNull(message = "error.email.template.update.object.version.number.cannot.be.empty", groups = {Update.class})
    @ApiModelProperty(value = "乐观所版本号")
    private Long objectVersionNumber;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class EmailTemplateCreateVO extends TemplateCreateVO {
        @NotEmpty(message = "error.email.template.create.email.title.cannot.be.empty", groups = {Insert.class, Update.class})
        @ApiModelProperty(value = "邮件标题")
        private String emailTitle;
        @NotEmpty(message = "error.email.template.create.email.content.cannot.be.empty", groups = {Insert.class, Update.class})
        @ApiModelProperty(value = "邮件内容")
        private String emailContent;

        public String getEmailTitle() {
            return emailTitle;
        }

        public void setEmailTitle(String emailTitle) {
            this.emailTitle = emailTitle;
        }

        public String getEmailContent() {
            return emailContent;
        }

        public void setEmailContent(String emailContent) {
            this.emailContent = emailContent;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PmTemplateCreateVO extends TemplateCreateVO {
        @NotEmpty(message = "error.pm.template.create.pm.title.cannot.be.empty", groups = {Insert.class, Update.class})
        @ApiModelProperty(value = "站内信主题")
        private String pmTitle;

        @NotEmpty(message = "error.pm.template.create.pm.content.cannot.be.empty", groups = {Insert.class, Update.class})
        @ApiModelProperty(value = "站内信内容")
        private String pmContent;

        @ApiModelProperty(value = "站内信类型")
        private String pmType;

        public String getPmTitle() {
            return pmTitle;
        }

        public void setPmTitle(String pmTitle) {
            this.pmTitle = pmTitle;
        }

        public String getPmContent() {
            return pmContent;
        }

        public void setPmContent(String pmContent) {
            this.pmContent = pmContent;
        }

        public String getPmType() {
            return pmType;
        }

        public void setPmType(String pmType) {
            this.pmType = pmType;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SmsTemplateCreateVO extends TemplateCreateVO {
        @NotEmpty(message = "error.sms.template.create.sms.content.cannot.be.empty", groups = {Insert.class, Update.class})
        @ApiModelProperty(value = "短信内容")
        private String smsContent;

        public String getSmsContent() {
            return smsContent;
        }

        public void setSmsContent(String smsContent) {
            this.smsContent = smsContent;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public Boolean getPredefined() {
        return predefined;
    }

    public void setPredefined(Boolean predefined) {
        this.predefined = predefined;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}
