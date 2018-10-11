package io.choerodon.notify.api.dto;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class WsSendDTO {

    @NotNull(message = "error.postPm.idNull")
    private Long id;

    @NotEmpty(message = "error.postPm.codeEmpty")
    private String code;

    private Map<String, Object> params;

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

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
