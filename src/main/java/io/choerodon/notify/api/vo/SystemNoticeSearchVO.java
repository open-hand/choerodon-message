package io.choerodon.notify.api.vo;

import io.choerodon.base.constant.PageConstant;
/**
 * @author wkj
 * @since 2019/9/9 16:46:00
 */
public class SystemNoticeSearchVO {
    private int page = Integer.valueOf(PageConstant.PAGE);
    private int size = Integer.valueOf(PageConstant.SIZE);
    private String title;
    private String content;
    private String status;
    private Boolean sendNotices;
    private String[] params;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getSendNotices() {
        return sendNotices;
    }

    public void setSendNotices(Boolean sendNotices) {
        this.sendNotices = sendNotices;
    }

    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }
}
