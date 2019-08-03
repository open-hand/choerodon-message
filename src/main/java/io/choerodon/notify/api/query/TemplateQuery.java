package io.choerodon.notify.api.query;

/**
 * @author superlee
 * @since 2019-05-21
 */
public class TemplateQuery {

    private String code;

    private String name;

    private String type;

    private Boolean predefined;

    private String params;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getPredefined() {
        return predefined;
    }

    public void setPredefined(Boolean predefined) {
        this.predefined = predefined;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }
}
