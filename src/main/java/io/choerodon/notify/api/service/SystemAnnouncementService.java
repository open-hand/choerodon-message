package io.choerodon.notify.api.service;


import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.notify.api.dto.SystemAnnouncementDTO;

/**
 * @author dengyouquan
 **/
public interface SystemAnnouncementService {
    SystemAnnouncementDTO create(SystemAnnouncementDTO dto);

    /**
     * 分页查询系统公告
     *
     * @param pageRequest
     * @param title
     * @param content
     * @param param
     * @return
     */
    Page<SystemAnnouncementDTO> pagingQuery(PageRequest pageRequest, String title, String content, String param);
}
