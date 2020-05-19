package io.choerodon.message.app.service;


import io.choerodon.core.domain.Page;
import io.choerodon.message.api.vo.SystemAnnouncementVO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author dengyouquan , Eugen
 **/
public interface SystemAnnouncementService {

    /**
     * 创建系统公告.
     *
     * @param vo 系统公告DTO
     * @return 系统公告DTO
     */
    SystemAnnouncementVO create(SystemAnnouncementVO vo);

    /**
     * 更新系统公告.
     *
     * @param vo 系统公告vo
     * @return 系统公告vo
     */
    SystemAnnouncementVO update(SystemAnnouncementVO vo);

    /**
     * 分页查询系统公告.
     *
     * @param pageRequest
     * @param title
     * @param status
     * @param param
     * @return
     */
    Page<SystemAnnouncementVO> pagingQuery(PageRequest pageRequest, String title, String status, String param);

    /**
     * 根据id获取系统公告详情.
     *
     * @param id 系统公告ID
     * @return 系统公告详情
     */
    SystemAnnouncementVO getDetailById(Long id);

    /**
     * 根据id删除系统公告
     *
     * @param id 系统公告ID
     */
    void delete(Long id);

    /**
     * 获取当前需悬浮显示的最新的系统公告
     *
     * @return 系统公告
     */
    SystemAnnouncementVO getLatestSticky();
}

