package io.choerodon.message.infra.dto;

import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

/**
 * @author jiameng.cao
 * @date 2019/10/25
 */
@Table(name = "notify_receive_setting")
public class ReceiveSettingDTO extends AuditDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 弃用该字段，使用 code 字段代替
     * 该字段的含义是sendSetting的id
     * 直接改成sendSetting的sendSettingCode
     */
    @Deprecated
    private Long sendSettingId;

    @ApiModelProperty(value = "sendSetting的sendSettingCode")
    private String sendSettingCode;
    private Long sourceId;
    private String sourceType;
    private Long userId;
    private String sendingType;

    public ReceiveSettingDTO() {
    }

    public ReceiveSettingDTO(Long sendSettingId, String sendingType, Long sourceId, String sourceType, Long userId) {
        this.sendSettingId = sendSettingId;
        this.sourceId = sourceId;
        this.sourceType = sourceType;
        this.userId = userId;
        this.sendingType = sendingType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSendSettingId() {
        return sendSettingId;
    }

    public void setSendSettingId(Long sendSettingId) {
        this.sendSettingId = sendSettingId;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSendSettingCode() {
        return sendSettingCode;
    }

    public void setSendSettingCode(String sendSettingCode) {
        this.sendSettingCode = sendSettingCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReceiveSettingDTO that = (ReceiveSettingDTO) o;
        return Objects.equals(getSendSettingId(), that.getSendSettingId()) &&
                Objects.equals(getSendingType(), that.getSendingType()) &&
                Objects.equals(getSourceId(), that.getSourceId()) &&
                Objects.equals(getSourceType(), that.getSourceType()) &&
                Objects.equals(getUserId(), that.getUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSendSettingId(), getSendingType(), getSourceId(), getSourceType(), getUserId());
    }

    public String getSendingType() {
        return sendingType;
    }

    public void setSendingType(String sendingType) {
        this.sendingType = sendingType;
    }
}
