package io.choerodon.notify.api.vo;

/**
 * 系统公告查询VO.
 *
 * @author wkj
 * @since 2019/9/9
 */
public class SystemNoticeSearchVO {
    private String title;
    private String content;
    private String status;
    private String[] params;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }
}
