package io.choerodon.notify.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.choerodon.notify.infra.dto.Template;
import org.modelmapper.PropertyMap;

public class TemplateQueryDTO {

    private Long id;

    private String name;

    private String code;

    private String type;

    private Boolean isPredefined;

    @JsonIgnore
    private String params;

    public TemplateQueryDTO(String name, String code, String type, Boolean isPredefined, String params) {
        this.name = name;
        this.code = code;
        this.type = type;
        this.isPredefined = isPredefined;
        this.params = params;
    }

    public static PropertyMap<TemplateQueryDTO, Template> dto2Entity() {
        return new PropertyMap<TemplateQueryDTO, Template>() {
            @Override
            protected void configure() {
                skip().setSendingType(null);
                skip().setContent(null);
                skip().setCreatedBy(null);
                skip().setCreationDate(null);
                skip().setTitle(null);
                skip().setObjectVersionNumber(null);
                skip().setLastUpdateDate(null);
                skip().setLastUpdatedBy(null);

                skip().set__id(null);
                skip().set__tls(null);
                skip().set__status(null);
                skip().setSortname(null);
                skip().setSortorder(null);
                skip().set_token(null);
                skip().setRequestId(null);
                skip().setProgramId(null);
                skip().setAttributeCategory(null);
                skip().setAttribute1(null);
                skip().setAttribute2(null);
                skip().setAttribute3(null);
                skip().setAttribute4(null);
                skip().setAttribute5(null);
                skip().setAttribute6(null);
                skip().setAttribute7(null);
                skip().setAttribute8(null);
                skip().setAttribute9(null);
                skip().setAttribute10(null);
                skip().setAttribute11(null);
                skip().setAttribute12(null);
                skip().setAttribute13(null);
                skip().setAttribute14(null);
                skip().setAttribute15(null);

                map().setSendSettingCode(source.getType());
            }
        };
    }

    public static PropertyMap<Template, TemplateQueryDTO> entity2Dto() {
        return new PropertyMap<Template, TemplateQueryDTO>() {
            @Override
            protected void configure() {
                map().setType(source.getSendSettingCode());
                skip().setParams(null);
            }
        };
    }

    public TemplateQueryDTO() {
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
}
