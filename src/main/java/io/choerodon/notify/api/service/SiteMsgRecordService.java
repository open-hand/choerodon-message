package io.choerodon.notify.api.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.notify.api.dto.SiteMsgRecordDTO;
import io.choerodon.notify.api.pojo.SiteMsgRecordQueryParam;

import java.util.List;

/**
 * @author dengyouquan
 **/
public interface SiteMsgRecordService {

    /**
     * 分页查询用户所有未删除消息
     *
     * @return
     */
    Page<SiteMsgRecordDTO> pagingQueryByUserId(Long userId, PageRequest pageRequest);

    /**
     * 查询用户所有的未读消息接口
     *
     * @return
     */
    List<SiteMsgRecordDTO> listByReadAndUserId(Long userId, Boolean isRead);

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
