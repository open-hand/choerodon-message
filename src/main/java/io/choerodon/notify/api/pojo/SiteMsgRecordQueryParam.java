package io.choerodon.notify.api.pojo;


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


    private String params;

    public SiteMsgRecordQueryParam() {
    }

    public SiteMsgRecordQueryParam(Long userId, Boolean deleted) {
        this.userId = userId;
        this.deleted = deleted;
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

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }
}
