package io.choerodon.notify.api.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author superlee
 */
public class OrganizationDTO {

    @ApiModelProperty(value = "主键/非必填")
    private Long id;
    
    @ApiModelProperty(value = "组织名/必填")
    private String name;
    
    @ApiModelProperty(value = "组织编码/必填")
    private String code;
    
    @ApiModelProperty(value = "乐观锁版本号")
    private Long objectVersionNumber;
    
    @ApiModelProperty(value = "是否启用/非必填/默认：true")
    private Boolean enabled;

    @ApiModelProperty(value = "项目数量")
    private Integer projectCount;

    @ApiModelProperty(value = "组织图标url")
    private String imageUrl;

    private Long userId;

    private String address;

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

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
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

    public Integer getProjectCount() {
        return projectCount;
    }

    public void setProjectCount(Integer projectCount) {
        this.projectCount = projectCount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
