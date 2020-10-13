package io.choerodon.message.api.vo;

import org.hzero.starter.keyencrypt.core.Encrypt;


/**
 * Created by wangxiang on 2020/10/13
 */
public class Role {
    @Encrypt
    private Long id;

    private String name;

    private String code;

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
}
