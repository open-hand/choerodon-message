package io.choerodon.notify.api.dto;

import io.choerodon.notify.domain.SiteMsgRecord;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;
import org.modelmapper.PropertyMap;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author dengyouquan
 **/
public class SiteMsgRecordDTO {
    private Long id;

    @ApiModelProperty(value = "站内信用户id,确保用户id已存在/必填")
    @NotNull(message = "error.siteMsgRecord.userIdNull")
    private Long userId;

    @ApiModelProperty(value = "站内信标题/必填")
    @NotEmpty(message = "error.siteMsgRecord.titleEmpty")
    private String title;

    @ApiModelProperty(value = "站内信内容/必填")
    @NotEmpty(message = "error.siteMsgRecord.contentEmpty")
    private String content;

    private Boolean read;

    private Boolean deleted;

    private Date sendTime;

    private Long objectVersionNumber;

    public static PropertyMap<SiteMsgRecordDTO, SiteMsgRecord> dto2Entity() {
        return new PropertyMap<SiteMsgRecordDTO, SiteMsgRecord>() {
            protected void configure() {
                skip().setCreatedBy(null);
                skip().setCreationDate(null);
                skip().setLastUpdateDate(null);
                skip().setLastUpdatedBy(null);
            }
        };
    }

    public static PropertyMap<SiteMsgRecord, SiteMsgRecordDTO> entity2Dto() {
        return new PropertyMap<SiteMsgRecord, SiteMsgRecordDTO>() {
            @Override
            protected void configure() {
                skip().setSiteMsgSend(null);
            }
        };
    }

    public void setSiteMsgSend(SiteMsgSendDTO siteMsgSend) {
        if (siteMsgSend == null) return;
        this.setTitle(siteMsgSend.getTitle());
        this.setContent(siteMsgSend.getContent());
        this.setUserId(siteMsgSend.getUserId());
    }

    @Override
    public String toString() {
        return "SiteMsgRecordDTO{" +
                "id=" + id +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", read=" + read +
                ", deleted=" + deleted +
                ", sendTime=" + sendTime +
                ", objectVersionNumber=" + objectVersionNumber +
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

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}
