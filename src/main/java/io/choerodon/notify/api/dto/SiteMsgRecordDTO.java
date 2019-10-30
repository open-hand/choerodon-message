package io.choerodon.notify.api.dto;

import io.choerodon.notify.infra.dto.SiteMsgRecord;
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

    private Boolean backlogFlag;

    private Boolean read;

    private Long sendBy;

    private String senderType;

    private Date sendTime;

    private Long objectVersionNumber;

    private UserDTO sendByUser;

    private ProjectDTO sendByProject;

    private OrganizationDTO sendByOrganization;

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

    public UserDTO getSendByUser() {
        return sendByUser;
    }

    public void setSendByUser(UserDTO sendByUser) {
        this.sendByUser = sendByUser;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public String getSenderType() {
        return senderType;
    }

    public void setSenderType(String senderType) {
        this.senderType = senderType;
    }

    public ProjectDTO getSendByProject() {
        return sendByProject;
    }

    public void setSendByProject(ProjectDTO sendByProject) {
        this.sendByProject = sendByProject;
    }

    public OrganizationDTO getSendByOrganization() {
        return sendByOrganization;
    }

    public void setSendByOrganization(OrganizationDTO sendByOrganization) {
        this.sendByOrganization = sendByOrganization;
    }

    @Override
    public String toString() {
        return "SiteMsgRecordDTO{" +
                "id=" + id +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", backlogFlag='" + backlogFlag + '\'' +
                ", read=" + read +
                ", sendBy=" + sendBy +
                ", senderType='" + senderType + '\'' +
                ", sendTime=" + sendTime +
                ", objectVersionNumber=" + objectVersionNumber +
                ", sendByUser=" + sendByUser +
                ", sendByProject=" + sendByProject +
                ", sendByOrganization=" + sendByOrganization +
                '}';
    }

    public Boolean getBacklogFlag() {
        return backlogFlag;
    }

    public void setBacklogFlag(Boolean backlogFlag) {
        this.backlogFlag = backlogFlag;
    }
}
