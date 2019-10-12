package io.choerodon.notify.api.service;


import com.github.pagehelper.PageInfo;

import org.springframework.data.domain.*;
import io.choerodon.core.iam.*;
import io.choerodon.notify.api.dto.*;

/**
 * @author dengyouquan , Eugen
 **/
public interface SystemAnnouncementService {

    /**
     * 创建系统公告.
     *
     * @param dto 系统公告DTO
     * @return 系统公告DTO
     */
    SystemAnnouncementDTO create(SystemAnnouncementDTO dto);

    /**
     * 更新系统公告.
     *
     * @param dto      系统公告DTO
     * @param level    层级
     * @param sourceId 组织/项目Id 平台层为:0
     * @return 系统公告DTO
     */
    SystemAnnouncementDTO update(SystemAnnouncementDTO dto, ResourceLevel level, Long sourceId);

    /**
     * 分页查询系统公告.
     * @param pageable
     * @param title
     * @param status
     * @param param
     * @return
     */
    PageInfo<SystemAnnouncementDTO> pagingQuery(Pageable pageable, String title, String status, String param);

    /**
     * 根据id获取系统公告详情.
     *
     * @param id 系统公告ID
     * @return 系统公告详情
     */
    SystemAnnouncementDTO getDetailById(Long id);

    /**
     * 根据id删除系统公告
     *
     * @param id 系统公告ID
     */
    void delete(Long id);

    /**
     * 系统公告的可执行程序具体方法：
     * 是否发送站内信？向所有用户发送站内信（异步）：不发送站内信
     * 更改
     *
     * @param sourceType           层级
     * @param sourceId             组织/项目Id 平台层为:0
     * @param systemNotificationId 系统公告Id
     */
    void sendSystemNotification(ResourceLevel sourceType, Long sourceId, Long systemNotificationId);

    /**
     * 获取当前需悬浮显示的最新的系统公告
     *
     * @return 系统公告
     */
    SystemAnnouncementDTO getLatestSticky();
}

