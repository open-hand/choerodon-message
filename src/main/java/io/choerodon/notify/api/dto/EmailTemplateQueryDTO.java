package io.choerodon.notify.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.notify.domain.Template;
import org.modelmapper.PropertyMap;

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

    public EmailTemplateQueryDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getIsPredefined() {
        return isPredefined;
    }

    public void setIsPredefined(Boolean predefined) {
        isPredefined = predefined;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public PageRequest getPageRequest() {
        return pageRequest;
    }

    public void setPageRequest(PageRequest pageRequest) {
        this.pageRequest = pageRequest;
    }
}
