package io.choerodon.notify.api.dto;

/**
 * @author dengyouquan
 **/
public class SiteMsgSendDTO {

    private Long userId;

    private String title;

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
