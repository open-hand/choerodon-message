package io.choerodon.notify.api.pojo;

import io.choerodon.mybatis.pagehelper.domain.PageRequest;

public class RecordQueryParam {
    private PageRequest pageRequest;
    private String status;
    private String email;
    private String templateType;
    private String retryStatus;
    private String failedReason;
    private String params;
    private String level;

    public RecordQueryParam() {
    }

    public RecordQueryParam(String status, String email,
                            String templateType, String retryStatus,
                            String failedReason, String params, String level) {
        this.status = status;
        this.email = email;
        this.templateType = templateType;
        this.retryStatus = retryStatus;
        this.failedReason = failedReason;
        this.params = params;
        this.level = level;
    }

    public PageRequest getPageRequest() {
        return pageRequest;
    }

    public void setPageRequest(PageRequest pageRequest) {
        this.pageRequest = pageRequest;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public String getRetryStatus() {
        return retryStatus;
    }

    public void setRetryStatus(String retryStatus) {
        this.retryStatus = retryStatus;
    }

    public String getFailedReason() {
        return failedReason;
    }

    public void setFailedReason(String failedReason) {
        this.failedReason = failedReason;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
