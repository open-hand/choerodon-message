package io.choerodon.notify.api.dto;

import io.choerodon.notify.domain.Template;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;
import org.modelmapper.PropertyMap;

@Getter
@Setter
@ToString
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

}
