package io.choerodon.notify.api.dto;

import io.choerodon.notify.domain.Template;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;
import org.modelmapper.PropertyMap;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class PmTemplateDTO {

    private static final String CODE_REGULAR_EXPRESSION = "^(?!.*?-$)[a-z][a-z0-9-]*$";

    private Long id;

    @ApiModelProperty(value = "模板编码/必填")
    @NotEmpty(message = "error.pmTemplate.codeEmpty")
    @Size(min = 1, max = 14, message = "error.pmTemplate.code.size")
    @Pattern(regexp = CODE_REGULAR_EXPRESSION, message = "error.pmTemplate.code.illegal")
    private String code;

    @ApiModelProperty(value = "模板名称/必填")
    @NotEmpty(message = "error.pmTemplate.nameEmpty")
    @Size(min = 1, max = 30, message = "error.pmTemplate.name.size")
    private String name;

    @ApiModelProperty(value = "是否预定义/非必填")
    private Boolean isPredefined;

    @ApiModelProperty(value = "模板类型/必填")
    @NotEmpty(message = "error.pmTemplate.typeEmpty")
    private String type;

    @ApiModelProperty(value = "模板标题/必填")
    @NotEmpty(message = "error.pmTemplate.titleEmpty")
    private String title;

    @ApiModelProperty(value = "模板内容/必填")
    @NotEmpty(message = "error.pmTemplate.contentEmpty")
    private String content;

    private Long objectVersionNumber;

    public static PropertyMap<PmTemplateDTO, Template> dto2Entity() {
        return new PropertyMap<PmTemplateDTO, Template>() {
            protected void configure() {
                skip().setMessageType(null);
                skip().setSmsContent(null);
                skip().setEmailTitle(null);
                skip().setEmailContent(null);
                skip().setCreatedBy(null);
                skip().setCreationDate(null);
                skip().setLastUpdateDate(null);
                skip().setLastUpdatedBy(null);
                map().setBusinessType(source.getType());
                map().setPmContent(source.getContent());
                map().setPmTitle(source.getTitle());
            }
        };
    }

    public static PropertyMap<Template, PmTemplateDTO> entity2Dto() {
        return new PropertyMap<Template, PmTemplateDTO>() {
            protected void configure() {
                map().setType(source.getBusinessType());
                map().setContent(source.getPmContent());
                map().setTitle(source.getPmTitle());
            }
        };
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

    public Boolean getIsPredefined() {
        return isPredefined;
    }

    public void setIsPredefined(Boolean predefined) {
        isPredefined = predefined;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}
