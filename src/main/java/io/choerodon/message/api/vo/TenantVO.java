package io.choerodon.message.api.vo;

/**
 * @author scp
 * @date 2020/5/6
 * @description
 */
public class TenantVO {
    private Long tenantId;
    private String tenantName;
    private String tenantNum;
    private Integer enabledFlag;
    private Integer limitUserQty;
    private String extInfo;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getTenantNum() {
        return tenantNum;
    }

    public void setTenantNum(String tenantNum) {
        this.tenantNum = tenantNum;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public Integer getLimitUserQty() {
        return limitUserQty;
    }

    public void setLimitUserQty(Integer limitUserQty) {
        this.limitUserQty = limitUserQty;
    }

    public String getExtInfo() {
        return extInfo;
    }

    public void setExtInfo(String extInfo) {
        this.extInfo = extInfo;
    }
}
