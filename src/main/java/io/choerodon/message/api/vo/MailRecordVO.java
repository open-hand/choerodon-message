package io.choerodon.message.api.vo;

import java.util.Date;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/7/16
 * @Modified By:
 */
public class MailRecordVO {
    private Long allCount;
    private Long successCount;
    private Date creationDate;

    public Long getAllCount() {
        return allCount;
    }

    public void setAllCount(Long allCount) {
        this.allCount = allCount;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Long getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Long successCount) {
        this.successCount = successCount;
    }


}
