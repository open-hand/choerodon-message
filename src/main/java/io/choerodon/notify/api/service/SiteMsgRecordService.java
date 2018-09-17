package io.choerodon.notify.api.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.notify.api.dto.SiteMsgRecordDTO;

/**
 * @author dengyouquan
 **/
public interface SiteMsgRecordService {

    /**
     * 分页查询用户所有未删除消息
     *
     * @return
     */
    Page<SiteMsgRecordDTO> pagingQueryByUserId(Long userId, Boolean isRead, PageRequest pageRequest);

    /**
     * 批量修改消息为已读
     *
     * @param list
     */
    void batchUpdateSiteMsgRecordIsRead(Long userId, Long[] ids);


    /**
     * 批量修改消息为已删除
     *
     * @param list
     */
    void batchUpdateSiteMsgRecordIsDeleted(Long userId, Long[] ids);
}
