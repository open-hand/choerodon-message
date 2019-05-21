package io.choerodon.notify.domain;

import java.util.Date;
import javax.persistence.*;

import io.choerodon.mybatis.entity.BaseDTO;

/**
 * @author dengyouquan
 **/
@Table(name = "notify_system_announcement")
public class SystemAnnouncement extends BaseDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    private Date sendDate;
    @Column(name = "is_send_notices")
    private Boolean sendNotices;
    private String status;
    @Column(name = "SCHEDULE_TASK_ID")
    private Long scheduleTaskId;
    @Column(name = "IS_STICKY")
    private Boolean sticky;
    private Date endDate;

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

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Boolean getSticky() {
        return sticky;
    }

    public void setSticky(Boolean sticky) {
        this.sticky = sticky;
    }
}
