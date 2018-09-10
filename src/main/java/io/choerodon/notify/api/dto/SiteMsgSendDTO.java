package io.choerodon.notify.api.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * @author dengyouquan
 **/
public class SiteMsgSendDTO {

    @ApiModelProperty(value = "站内信用户id,确保用户id已存在/必填")
    @NotNull(message = "error.siteMsgRecord.userIdNull")
    private Long userId;

    @ApiModelProperty(value = "站内信标题/必填")
    @NotEmpty(message = "error.siteMsgRecord.titleEmpty")
    private String title;

    @ApiModelProperty(value = "站内信内容/必填")
    @NotEmpty(message = "error.siteMsgRecord.contentEmpty")
    private String content;

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
}
