package io.choerodon.notify.domain;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * @author dengyouquan
 **/
@ModifyAudit
@VersionAudit
@Table(name = "notify_system_announcement")
public class SystemAnnouncement extends AuditDomain {
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String content;
    private Date sendDate;
    @Column(name = "is_send_notices")
    private Boolean sendNotices;
    private String status;
    @Column(name = "SCHEDULE_TASK_ID")
    private Long scheduleTaskId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public Boolean getSendNotices() {
        return sendNotices;
    }

    public void setSendNotices(Boolean sendNotices) {
        this.sendNotices = sendNotices;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getScheduleTaskId() {
        return scheduleTaskId;
    }

    public void setScheduleTaskId(Long scheduleTaskId) {
        this.scheduleTaskId = scheduleTaskId;
    }
}
