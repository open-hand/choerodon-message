package io.choerodon.notify.infra.dto;

import io.choerodon.mybatis.entity.BaseDTO;

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
public class ReceiveSettingDTO extends BaseDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long sendSettingId;
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
