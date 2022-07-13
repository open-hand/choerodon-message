package io.choerodon.message.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.core.base.BaseConstants;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @author dengyouquan
 **/
public class SystemAnnouncementVO {
    @Encrypt
    @ApiModelProperty(value = "系统公告ID/非必填")
    private Long id;

    @ApiModelProperty(value = "前端需要字段")
    private Long readId;

    @ApiModelProperty(value = "系统公告标题/必填")
    @NotEmpty(message = "error.announcement.title.empty")
    @Size(min = 1, max = 64, message = "error.announcement.title.length")
    private String title;
    @ApiModelProperty(value = "系统公告内容/必填")
    @NotEmpty(message = "error.announcement.content.empty")
    private String content;
    @ApiModelProperty(value = "系统公告开始时间(也就是发送时间)")
    @JsonFormat(pattern = BaseConstants.Pattern.DATETIME)
    private Date sendDate;
    @ApiModelProperty(value = "系统公告状态/非必填")
    private String status;
    @ApiModelProperty(value = "是否顶部悬浮显示")
    private Boolean sticky;
    @ApiModelProperty(value = "悬浮显示结束时间")
    @JsonFormat(pattern = BaseConstants.Pattern.DATETIME)
    private Date endDate;

    @ApiModelProperty(value = "乐观锁版本号")
    private Long objectVersionNumber;

    public Long getReadId() {
        return readId;
    }

    public void setReadId(Long readId) {
        this.readId = readId;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public String getStatus() {
        return status;
    }

    public Boolean getSticky() {
        return sticky;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public SystemAnnouncementVO setId(Long id) {
        this.id = id;
        return this;
    }

    public SystemAnnouncementVO setTitle(String title) {
        this.title = title;
        return this;
    }

    public SystemAnnouncementVO setContent(String content) {
        this.content = content;
        return this;
    }

    public SystemAnnouncementVO setSendDate(Date sendDate) {
        this.sendDate = sendDate;
        return this;
    }

    public SystemAnnouncementVO setStatus(String status) {
        this.status = status;
        return this;
    }

    public SystemAnnouncementVO setSticky(Boolean sticky) {
        this.sticky = sticky;
        return this;
    }

    public SystemAnnouncementVO setEndDate(Date endDate) {
        this.endDate = endDate;
        return this;
    }

    public SystemAnnouncementVO setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
        return this;
    }
}
