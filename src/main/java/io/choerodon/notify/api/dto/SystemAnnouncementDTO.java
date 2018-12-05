package io.choerodon.notify.api.dto;

import io.choerodon.notify.domain.SystemAnnouncement;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;
import org.modelmapper.PropertyMap;

import javax.validation.constraints.Size;
import java.util.Date;

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
    private Date sendDate;

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
}
