package io.choerodon.notify.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.notify.domain.Template;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.PropertyMap;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class EmailTemplateQueryDTO {

    private Long id;

    private String name;

    private String code;

    private String type;

    private Boolean isPredefined;

    @JsonIgnore
    private String params;

    @JsonIgnore
    private PageRequest pageRequest;


    public EmailTemplateQueryDTO(String name, String code, String type, Boolean isPredefined, String params, PageRequest pageRequest) {
        this.name = name;
        this.code = code;
        this.type = type;
        this.isPredefined = isPredefined;
        this.params = params;
        this.pageRequest = pageRequest;
    }

    public static PropertyMap<EmailTemplateQueryDTO, Template> dto2Entity() {
        return new PropertyMap<EmailTemplateQueryDTO, Template>() {
            protected void configure() {
                skip().setMessageType(null);
                skip().setSmsContent(null);
                skip().setCreatedBy(null);
                skip().setCreationDate(null);
                skip().setObjectVersionNumber(null);
                skip().setEmailTitle(null);
                skip().setEmailContent(null);
                skip().setLastUpdateDate(null);
                skip().setLastUpdatedBy(null);
                map().setBusinessType(source.getType());
            }
        };
    }

    public static PropertyMap<Template, EmailTemplateQueryDTO> entity2Dto() {
        return new PropertyMap<Template, EmailTemplateQueryDTO>() {
            protected void configure() {
                map().setType(source.getBusinessType());
                skip().setParams(null);
                skip().setPageRequest(null);
            }
        };
    }
}
