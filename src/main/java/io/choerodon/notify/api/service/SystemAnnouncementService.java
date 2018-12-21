package io.choerodon.notify.api.service;


import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.notify.api.dto.SystemAnnouncementDTO;

/**
 * @author dengyouquan
 **/
public interface SystemAnnouncementService {

    /**
     * 创建系统公告
     *
     * @param dto
     * @return
     */
    SystemAnnouncementDTO create(SystemAnnouncementDTO dto);

    /**
     * 更新系统公告
     *
     * @param dto
     * @param level
     * @param sourceId
     * @return
     */
    SystemAnnouncementDTO update(SystemAnnouncementDTO dto, ResourceLevel level, Long sourceId);

    /**
     * 分页查询系统公告
     *
     * @param pageRequest
     * @param title
     * @param content
     * @param param
     * @return
     */
    Page<SystemAnnouncementDTO> pagingQuery(PageRequest pageRequest, String title, String content, String param, String status, Boolean sendNotices);

    /**
     * 根据id获取系统公告详情
     *
     * @param id
     * @return
     */
    SystemAnnouncementDTO getDetailById(Long id);


    /**
     * 根据id删除系统公告
     *
     * @param id
     */
    void delete(Long id);


    /**
     * 系统公告的可执行程序具体方法：
     * 是否发送站内信？向所有用户发送站内信（异步）：不发送站内信
     * 更改
     *
     * @param sourceType           层级
     * @param sourceId             组织/项目Id 平台层为:0
     * @param systemNocificationId 系统公告Id
     */
    void sendSystemNotification(ResourceLevel sourceType, Long sourceId, Long systemNocificationId);

}
