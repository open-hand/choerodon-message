package io.choerodon.notify.api.dto;

import io.choerodon.notify.domain.Template;
import org.hibernate.validator.constraints.NotEmpty;
import org.modelmapper.PropertyMap;

public class EmailTemplateDTO {

    private Long id;

    @NotEmpty(message = "error.emailTemplate.codeEmpty")
    private String code;

    @NotEmpty(message = "error.emailTemplate.nameEmpty")
    private String name;

    private Boolean isPredefined;

    @NotEmpty(message = "error.emailTemplate.typeEmpty")
    private String type;

    @NotEmpty(message = "error.emailTemplate.titleEmpty")
    private String title;

    @NotEmpty(message = "error.emailTemplate.contentEmpty")
    private String content;

    private Long objectVersionNumber;

    public static PropertyMap<EmailTemplateDTO, Template> dto2Entity() {
        return new PropertyMap<EmailTemplateDTO, Template>() {
            protected void configure() {
                skip().setMessageType(null);
                skip().setSmsContent(null);
                skip().setCreatedBy(null);
                skip().setCreationDate(null);
                skip().setLastUpdateDate(null);
                skip().setLastUpdatedBy(null);
                map().setBusinessType(source.getType());
                map().setEmailContent(source.getContent());
            }
        };
    }

    public static PropertyMap<Template, EmailTemplateDTO> entity2Dto() {
        return new PropertyMap<Template, EmailTemplateDTO>() {
            protected void configure() {
                map().setType(source.getBusinessType());
                map().setContent(source.getEmailContent());
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
