package io.choerodon.message.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.core.base.BaseConstants;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @author dengyouquan
 **/
public class SystemAnnouncementVO {
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
    @JsonFormat(pattern = BaseConstants.Pattern.DATETIME)
    private Date sendDate;

    @ApiModelProperty(value = "是否发送站内信：不填时默认为发送")
    private Boolean sendNotices;
    @ApiModelProperty(value = "系统公告状态/非必填")
    private String status;
    @ApiModelProperty(value = "关联任务Id")
    private Long scheduleTaskId;
    @ApiModelProperty(value = "是否顶部悬浮显示")
    private Boolean sticky;
    @ApiModelProperty(value = "悬浮显示结束时间")
    @JsonFormat(pattern = BaseConstants.Pattern.DATETIME)
    private Date endDate;

    @ApiModelProperty(value = "乐观锁版本号")
    private Long objectVersionNumber;

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
        return scheduleTaskId;
    }

    public void setScheduleTaskId(Long scheduleTaskId) {
        this.scheduleTaskId = scheduleTaskId;
    }

    public Boolean getSticky() {
        return sticky;
    }

    public void setSticky(Boolean sticky) {
        this.sticky = sticky;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
