package io.choerodon.message.api.vo;

import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * @author jiameng.cao
 * @date 2019/10/29
 */
public class MsgServiceTreeVO {
    @Encrypt
    private Long parentId;

    @Encrypt
    private Long id;

    private String name;

    private String code;

    private Boolean enabled;

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
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

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
