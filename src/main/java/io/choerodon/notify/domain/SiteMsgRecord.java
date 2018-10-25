package io.choerodon.notify.domain;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.notify.api.pojo.PmType;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author dengyouquan
 * 站内信消息记录表
 **/
@ModifyAudit
@VersionAudit
@Table(name = "notify_sitemsg_record")
public class SiteMsgRecord extends AuditDomain {
    @Id
    @GeneratedValue
    private Long id;

    private Long userId;

    private String title;

    private String content;

    private String type;

    @Column(name = "is_read")
    private Boolean read;

    @Column(name = "is_deleted")
    private Boolean deleted;

    private Long sendBy;

    private Date sendTime;

    @Override
    public String toString() {
        return "SiteMsgRecord{" +
                "id=" + id +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", type=" + type +
                ", read=" + read +
                ", deleted=" + deleted +
                ", sendBy=" + sendBy +
                ", sendTime=" + sendTime +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Long getSendBy() {
        return sendBy;
    }

    public void setSendBy(Long sendBy) {
        this.sendBy = sendBy;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public SiteMsgRecord(Long userId, String title, String content) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.read = false;
        this.deleted = false;
        this.sendTime = new Date();
        this.setType(PmType.MSG.getValue());
    }

    public SiteMsgRecord() {
        this(null, null, null);
    }
}
