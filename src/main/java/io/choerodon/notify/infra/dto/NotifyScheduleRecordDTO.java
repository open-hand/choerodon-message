package io.choerodon.notify.infra.dto;

import io.choerodon.mybatis.entity.BaseDTO;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author wkj
 * @since 2019/10/31
 **/
@Table(name = "notify_schedule_record")
public class NotifyScheduleRecordDTO extends BaseDTO {
    @Id
    @GeneratedValue
    private Long id;
    @ApiModelProperty("任务id")
    private Long taskId;
    @ApiModelProperty("定时消息编码")
    private String scheduleNoticeCode;
    @ApiModelProperty("消息内容")
    private String noticeContent;

    public String getNoticeContent() {
        return noticeContent;
    }

    public void setNoticeContent(String noticeContent) {
        this.noticeContent = noticeContent;
    }

    public NotifyScheduleRecordDTO() { }

    public NotifyScheduleRecordDTO(Long taskId, String scheduleNoticeCode) {
        this.taskId = taskId;
        this.scheduleNoticeCode = scheduleNoticeCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getScheduleNoticeCode() {
        return scheduleNoticeCode;
    }

    public void setScheduleNoticeCode(String scheduleNoticeCode) {
        this.scheduleNoticeCode = scheduleNoticeCode;
    }
}
