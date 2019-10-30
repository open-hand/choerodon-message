package io.choerodon.notify.api.dto;

import java.util.Date;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;
import org.modelmapper.PropertyMap;

import io.choerodon.notify.infra.dto.SystemAnnouncement;

/**
 * @author dengyouquan
 **/
public class SystemAnnouncementDTO {
    @ApiModelProperty(value = "系统公告ID/非必填")
    private Long id;
    @ApiModelProperty(value = "系统公告标题/必填")
    @NotEmpty(message = "error.announcement.title.empty")
    @Size(min = 1, max = 64, message = "error.announcement.title.length")
    private String title;
    @ApiModelProperty(value = "系统公告内容/必填")
    @NotEmpty(message = "error.announcement.content.empty")
    private String content;

    @ApiModelProperty(value = "系统公告时间/非必填")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sendDate;

    @ApiModelProperty(value = "是否发送站内信：不填时默认为发送")
    private Boolean sendNotices;
    @ApiModelProperty(value = "系统公告状态/非必填")
    private String status;
    @ApiModelProperty(value = "关联任务Id")
    private Long ScheduleTaskId;
    @ApiModelProperty(value = "是否顶部悬浮显示")
    private Boolean sticky;
    @ApiModelProperty(value = "悬浮显示结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endDate;

    @ApiModelProperty(value = "乐观锁版本号")
    private Long objectVersionNumber;

    public static PropertyMap<SystemAnnouncementDTO, SystemAnnouncement> dto2Entity() {
        return new PropertyMap<SystemAnnouncementDTO, SystemAnnouncement>() {
            protected void configure() {
                //SystemAnnouncement 和 SystemAnnouncementDTO 字段完全相同，我们使用默认转换
            }
        };
    }


    public static PropertyMap<SystemAnnouncement, SystemAnnouncementDTO> entity2Dto() {
        return new PropertyMap<SystemAnnouncement, SystemAnnouncementDTO>() {
            protected void configure() {
                //SystemAnnouncement 和 SystemAnnouncementDTO 字段完全相同，我们使用默认转换
            }
        };
    }

    public enum AnnouncementStatus {
        COMPLETED("COMPLETED"),
        WAITING("WAITING");

        private final String value;

        private AnnouncementStatus(String value) {
            this.value = value;
        }

        public String value() {
            return this.value;
        }
    }

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

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
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
        return ScheduleTaskId;
    }

    public void setScheduleTaskId(Long scheduleTaskId) {
        ScheduleTaskId = scheduleTaskId;
    }

    public Boolean getSticky() {
        return sticky;
    }

    public void setSticky(Boolean sticky) {
        this.sticky = sticky;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
