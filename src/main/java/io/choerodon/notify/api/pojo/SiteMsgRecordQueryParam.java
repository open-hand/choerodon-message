package io.choerodon.notify.api.pojo;

import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author dengyouquan
 **/
public class SiteMsgRecordQueryParam {

    private Long id;

    private Long userId;

    private String title;

    private String content;

    private Boolean read;

    private Boolean deleted;

    private PageRequest pageRequest;

    private String params;

    public SiteMsgRecordQueryParam() {
    }

    public SiteMsgRecordQueryParam(Long userId, Boolean deleted, PageRequest pageRequest) {
        this.userId = userId;
        this.deleted = deleted;
        this.pageRequest = pageRequest;
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

    public PageRequest getPageRequest() {
        return pageRequest;
    }

    public void setPageRequest(PageRequest pageRequest) {
        this.pageRequest = pageRequest;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }
}
