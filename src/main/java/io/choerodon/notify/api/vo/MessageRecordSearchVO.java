package io.choerodon.notify.api.vo;

import io.choerodon.base.constant.PageConstant;
/**
 * @author wkj
 * @since 2019/9/9 16:46:00
 */
public class MessageRecordSearchVO {
    private int page = Integer.valueOf(PageConstant.PAGE);
    private int size = Integer.valueOf(PageConstant.SIZE);
    private String status;
    private String receiveEmail;
    private String templateType;
    private Integer retryStatus;
    private String failedReason;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReceiveEmail() {
        return receiveEmail;
    }

    public void setReceiveEmail(String receiveEmail) {
        this.receiveEmail = receiveEmail;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public Integer getRetryStatus() {
        return retryStatus;
    }

    public void setRetryStatus(Integer retryStatus) {
        this.retryStatus = retryStatus;
    }

    public String getFailedReason() {
        return failedReason;
    }

    public void setFailedReason(String failedReason) {
        this.failedReason = failedReason;
    }

    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }
}
