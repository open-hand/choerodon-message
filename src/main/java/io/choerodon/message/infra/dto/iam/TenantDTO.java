package io.choerodon.message.infra.dto.iam;

import java.util.List;
import java.util.Objects;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hzero.core.util.Regexs;

import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.domain.AuditDomain;

public class TenantDTO extends AuditDomain {

    public static final String TENANT_ID = "tenantId";
    public static final String TENANT_NAME = "tenantName";
    public static final String TENANT_NUM = "tenantNum";
    public static final String ENABLED_FLAG = "enabledFlag";

    public static final String NULL_VALUE = "";
    public static final String LIMIT_USER_QTY = "limitUserQty";


    /**
     * 校验传递的查询参数是否为""，若为""则转换为null
     */
    public void validateQueryCondition() {
        if (!Objects.isNull(tenantName) && Objects.equals(NULL_VALUE, tenantName)) {
            this.tenantName = null;
        }
        if (!Objects.isNull(tenantNum) && Objects.equals(NULL_VALUE, tenantNum)) {
            this.tenantNum = null;
        }
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    @ApiModelProperty("租户ID")
    private Long tenantId;
    @NotBlank
    @Length(max = 120)
    @MultiLanguageField
    @ApiModelProperty("租户名称")
    private String tenantName;
    @NotBlank
    @Length(max = 15)
    @ApiModelProperty("租户编号")
    @Pattern(regexp = Regexs.CODE)
    private String tenantNum;
    @NotNull
    @Range(max = 1, min = 0)
    @ApiModelProperty("是否启用")
    private Integer enabledFlag;
    @ApiModelProperty("限制用户数")
    private Integer limitUserQty;

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
}
