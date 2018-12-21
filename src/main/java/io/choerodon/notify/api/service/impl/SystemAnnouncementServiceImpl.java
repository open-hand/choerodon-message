package io.choerodon.notify.api.service.impl;


import java.util.*;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.choerodon.asgard.schedule.annotation.JobParam;
import io.choerodon.asgard.schedule.annotation.JobTask;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.notify.api.dto.ScheduleTaskDTO;
import io.choerodon.notify.api.dto.SystemAnnouncementDTO;
import io.choerodon.notify.api.service.SystemAnnouncementService;
import io.choerodon.notify.domain.SystemAnnouncement;
import io.choerodon.notify.infra.feign.AsgardFeignClient;
import io.choerodon.notify.infra.feign.UserFeignClient;
import io.choerodon.notify.infra.mapper.SystemAnnouncementMapper;
import io.choerodon.notify.infra.utils.AsyncSendAnnouncementUtils;

/**
 * @author dengyouquan
 **/
@Component
public class SystemAnnouncementServiceImpl implements SystemAnnouncementService {

    public static final String SITE_NOTYFICATION_CODE = "systemNotification";
    private static final Logger logger = LoggerFactory.getLogger(SystemAnnouncementServiceImpl.class);

    private final ModelMapper modelMapper = new ModelMapper();
    private final UserFeignClient userFeignClient;

    private AsyncSendAnnouncementUtils asyncSendAnnouncementUtils;
    private SystemAnnouncementMapper announcementMapper;
    private AsgardFeignClient asgardFeignClient;

    public SystemAnnouncementServiceImpl(AsyncSendAnnouncementUtils asyncSendAnnouncementUtils, SystemAnnouncementMapper announcementMapper, UserFeignClient userFeignClient, AsgardFeignClient asgardFeignClient) {
        this.announcementMapper = announcementMapper;
        this.userFeignClient = userFeignClient;
        this.asyncSendAnnouncementUtils = asyncSendAnnouncementUtils;
        this.asgardFeignClient = asgardFeignClient;
    }

    @Override
    public SystemAnnouncementDTO create(SystemAnnouncementDTO dto) {
        //1.创建系统公告
        logger.info("notify create system announcement,sendDate:" + dto.getSendDate());
        SystemAnnouncement systemAnnouncement = modelMapper.map(dto, SystemAnnouncement.class);
        if (announcementMapper.insert(systemAnnouncement) != 1) {
            throw new CommonException("error.systemAnnouncement.create");
        }
        //2.创建系统公告同时创建定时任务
        Long scheduleTaskId = createScheduleTask(systemAnnouncement);
        //3.更新系统公告关联任务id
        systemAnnouncement = announcementMapper.selectByPrimaryKey(systemAnnouncement.getId());
        systemAnnouncement.setScheduleTaskId(scheduleTaskId);
        if (announcementMapper.updateByPrimaryKeySelective(systemAnnouncement) != 1) {
            throw new CommonException("error.update.systemAnnouncement.scheduleTaskId");
        }
        return modelMapper.map(systemAnnouncement, SystemAnnouncementDTO.class);
    }

    @Override
    public SystemAnnouncementDTO update(SystemAnnouncementDTO dto, ResourceLevel level, Long sourceId) {
        //0.若系统公告已经完成
        SystemAnnouncement originSA = announcementMapper.selectByPrimaryKey(dto.getId());
        if (originSA == null) {
            throw new CommonException("error.update.system.announcement.not.exist,id:" + dto.getId());
        }
        if (originSA.getStatus().equalsIgnoreCase(SystemAnnouncementDTO.AnnouncementStatus.COMPLETED.value())) {
            throw new CommonException("error.update.systemAnnouncement.has.been.completed");
        }
        //1.创建新任务
        SystemAnnouncement systemAnnouncement = modelMapper.map(dto, SystemAnnouncement.class);
        Long scheduleTaskId = createScheduleTask(systemAnnouncement);
        //2.更新系统公告
        systemAnnouncement.setScheduleTaskId(scheduleTaskId);
        if (announcementMapper.updateByPrimaryKeySelective(systemAnnouncement) != 1) {
            //2.1.更新公告失败删除新建任务
            asgardFeignClient.deleteSiteTaskByTaskId(scheduleTaskId);
            throw new CommonException("error.systemAnnouncement.update");
        }
        //3.更新公告成功删除原任务
        asgardFeignClient.deleteSiteTaskByTaskId(originSA.getScheduleTaskId());

        return modelMapper.map(systemAnnouncement, SystemAnnouncementDTO.class);
    }

    /**
     * 根据系统公告创建任务
     *
     * @param systemAnnouncement
     * @return 任务Id
     */
    private Long createScheduleTask(SystemAnnouncement systemAnnouncement) {
        Long methodId = asgardFeignClient.getMethodIdByCode(SITE_NOTYFICATION_CODE).getBody();

        Long[] assignUserIds = new Long[1];
        assignUserIds[0] = DetailsHelper.getUserDetails().getUserId();

        Map<String, Object> params = new HashMap<>();
        params.put("systemNocificationId", systemAnnouncement.getId());

        ScheduleTaskDTO createTskDTO = new ScheduleTaskDTO(
                methodId, params, "系统公告", systemAnnouncement.getTitle(), systemAnnouncement.getSendDate(), assignUserIds);

        return asgardFeignClient.createSiteScheduleTask(createTskDTO).getBody().getId();
    }


    @Override
    public Page<SystemAnnouncementDTO> pagingQuery(PageRequest pageRequest, String title, String content, String param, String status, Boolean sendNotices) {
        return PageHelper.doPageAndSort(pageRequest, () ->
                announcementMapper.fulltextSearch(title, content, status, sendNotices, param));
    }

    @Override
    public SystemAnnouncementDTO getDetailById(Long id) {
        SystemAnnouncement systemAnnouncement = announcementMapper.selectByPrimaryKey(id);
        if (systemAnnouncement == null) {
            throw new CommonException("error.system.announcement.not.exist,id:" + id);
        }
        return modelMapper.map(systemAnnouncement, SystemAnnouncementDTO.class);
    }


    @Override
    public void delete(Long id) {
        SystemAnnouncement systemAnnouncement = announcementMapper.selectByPrimaryKey(id);
        if (systemAnnouncement == null) {
            throw new CommonException("error.delete.system.announcement.not.exist,id:" + id);
        }
        //1.删除系统公告
        if (announcementMapper.deleteByPrimaryKey(id) != 1) {
            throw new CommonException("error.system.announcement.delete.failed,id:" + id);
        }
        //2.删除任务
        asgardFeignClient.deleteSiteTaskByTaskId(systemAnnouncement.getScheduleTaskId());
    }

    /**
     * 系统公告JobTask
     *
     * @param map 参数map
     */
    @JobTask(maxRetryCount = 0, code = "systemNotification", params = {
            @JobParam(name = "systemNocificationId", description = "系统公告Id", type = Long.class)
    }, description = "平台层发送系统通知")
    public void systemNotification(Map<String, Object> map) {
        Long systemNocificationId = Optional.ofNullable(new Long((Integer) map.get("systemNocificationId"))).orElseThrow(() -> new CommonException("error.systemNotification.id.empty"));
        sendSystemNotification(ResourceLevel.SITE, 0L, systemNocificationId);
    }


    @Override
    public void sendSystemNotification(ResourceLevel sourceType, Long sourceId, Long systemNocificationId) {
        //todo 此处只实现平台层系统公告 待实现组织/项目层公告
        //根据公告id查询公告
        SystemAnnouncement systemAnnouncement = announcementMapper.selectByPrimaryKey(systemNocificationId);
        if (systemAnnouncement == null) {
            throw new CommonException("error.systemAnnouncement.empty,id:" + systemNocificationId);
        }
        //如果系统公告“是否发送站内信”字段为“是”，则发送站内信
        if (systemAnnouncement.getSendNotices()) {
            //发送设置模板:平台层
            String code = SITE_NOTYFICATION_CODE;
            //发送对象：全体用户
            List<Long> allUsersId = Arrays.asList(userFeignClient.getUserIds().getBody());
            //设置发送内容
            Map<String, Object> params = new HashMap<>();
            params.put("title", systemAnnouncement.getTitle());
            params.put("content", systemAnnouncement.getContent());
            asyncSendAnnouncementUtils.sendNoticeToAll(null, allUsersId, code, params, sourceId);
        }
        //更新公告状态
        systemAnnouncement.setStatus(SystemAnnouncementDTO.AnnouncementStatus.COMPLETED.value());
        if (announcementMapper.updateByPrimaryKey(systemAnnouncement) != 1) {
            throw new CommonException("error.systemAnnouncement.update");
        }
    }

}
