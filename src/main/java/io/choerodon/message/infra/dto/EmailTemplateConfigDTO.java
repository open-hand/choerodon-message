package io.choerodon.message.infra.dto;

import javax.persistence.Table;

import org.hzero.starter.keyencrypt.core.Encrypt;

@Table(name = "notify_receive_setting")
public class EmailTemplateConfigDTO {
    @Encrypt
    private Long id;
    private String logo;
    private String slogan;
    private String footer;

    private Long tenantId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}
