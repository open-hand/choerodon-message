package io.choerodon.message.api.vo;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * Created by wangxiang on 2022/3/24
 */
public class OpenAppVO {
    @ApiModelProperty("表Id")
    @Encrypt
    private Long id;
    @ApiModelProperty("应用类型")
    private String type;
    @ApiModelProperty("租户Id")
    private Long tenantId;
    @ApiModelProperty("第三方平台方appid")
    private String appId;
    @ApiModelProperty("appid对应的秘钥")
    private String appSecret;
    @ApiModelProperty("是否启用")
    private Boolean enabledFlag;

    private OpenAppConfigVO openAppConfigVO;

    public OpenAppConfigVO getOpenAppConfigVO() {
        return openAppConfigVO;
    }

    public void setOpenAppConfigVO(OpenAppConfigVO openAppConfigVO) {
        this.openAppConfigVO = openAppConfigVO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public Boolean getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Boolean enabledFlag) {
        this.enabledFlag = enabledFlag;
    }
}
