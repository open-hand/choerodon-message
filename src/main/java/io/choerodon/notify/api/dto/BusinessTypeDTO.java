package io.choerodon.notify.api.dto;

import java.util.Objects;

public class BusinessTypeDTO {

    private Long id;
    private String code;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BusinessTypeDTO that = (BusinessTypeDTO) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {

        return Objects.hash(code);
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

    public BusinessTypeDTO() {
    }

    public BusinessTypeDTO(Long id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }
}
