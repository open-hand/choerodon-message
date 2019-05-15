package io.choerodon.notify.domain;


import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author dengyouquan
 **/
@Table(name = "notify_receive_setting")
public class ReceiveSetting extends BaseDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long sendSettingId;
    private String messageType;
    @Column(name = "is_disabled")
    private Boolean disable;
    private Long sourceId;
    private String sourceType;
    private Long userId;

    public ReceiveSetting() {
    }

    public ReceiveSetting(Long sendSettingId, String messageType, Long sourceId, String sourceType, Long userId) {
        this.sendSettingId = sendSettingId;
        this.messageType = messageType;
        this.sourceId = sourceId;
        this.sourceType = sourceType;
        this.userId = userId;
        this.disable = true;
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

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public Boolean getDisable() {
        return disable;
    }

    public void setDisable(Boolean disable) {
        this.disable = disable;
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
        ReceiveSetting that = (ReceiveSetting) o;
        return Objects.equals(getSendSettingId(), that.getSendSettingId()) &&
                Objects.equals(getMessageType(), that.getMessageType()) &&
                Objects.equals(getSourceId(), that.getSourceId()) &&
                Objects.equals(getSourceType(), that.getSourceType()) &&
                Objects.equals(getUserId(), that.getUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSendSettingId(), getMessageType(), getSourceId(), getSourceType(), getUserId());
    }
}
