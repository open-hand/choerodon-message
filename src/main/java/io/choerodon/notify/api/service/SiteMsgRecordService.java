package io.choerodon.notify.api.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.notify.api.dto.SiteMsgRecordDTO;
import io.choerodon.notify.domain.Template;

/**
 * @author dengyouquan
 **/
public interface SiteMsgRecordService {

    /**
     * 分页查询用户所有未删除消息
     */
    PageInfo<SiteMsgRecordDTO> pagingQueryByUserId(Long userId, Boolean isRead, String type, int page, int size);

    /**
     * 批量修改消息为已读
     */
    void batchUpdateSiteMsgRecordIsRead(Long userId, Long[] ids);


    /**
     * 批量修改消息为已删除
     */
    void batchUpdateSiteMsgRecordIsDeleted(Long userId, Long[] ids);

    void insertRecord(Template template, String pmContent, Long[] ids);
}
