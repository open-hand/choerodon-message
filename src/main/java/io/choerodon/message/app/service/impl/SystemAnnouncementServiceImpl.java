package io.choerodon.message.app.service.impl;


import io.choerodon.core.domain.Page;
import io.choerodon.message.api.vo.SystemAnnouncementVO;
import io.choerodon.message.app.service.SystemAnnouncementService;
import io.choerodon.message.infra.dto.iam.TenantDTO;
import io.choerodon.message.infra.mapper.SystemAnnouncementMapper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.message.api.dto.NoticeDTO;
import org.hzero.message.app.service.NoticeReceiverService;
import org.hzero.message.app.service.NoticeService;
import org.hzero.message.domain.entity.Notice;
import org.hzero.message.domain.entity.NoticeContent;
import org.hzero.message.domain.entity.NoticeReceiver;
import org.hzero.message.domain.repository.NoticeContentRepository;
import org.hzero.message.domain.repository.NoticeRepository;
import org.hzero.message.infra.mapper.NoticePublishedMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author dengyouquan
 **/
@Component
public class SystemAnnouncementServiceImpl implements SystemAnnouncementService {

    // TODO 注释的内容如果没有使用，发版前需要删除， 版本是0.22.0
    public static final String SITE_NOTYFICATION_CODE = "systemNotification";

    private static final Logger logger = LoggerFactory.getLogger(SystemAnnouncementServiceImpl.class);

    private static final String LANG = "zh_CN";
    private static final String RECEIVER_TYPE_CODE_ANNOUNCE = "ANNOUNCE";
    private static final String NOTICE_TYPE_CODE_PTGG = "PTGG";

    //    private AsyncSendAnnouncementUtils asyncSendAnnouncementUtils;
    @Autowired
    private SystemAnnouncementMapper announcementMapper;

    @Autowired
    private NoticePublishedMapper noticePublishedMapper;

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private NoticeContentRepository noticeContentRepository;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private NoticeReceiverService noticeReceiverService;

    public SystemAnnouncementServiceImpl() {
    }

    @Override
    public SystemAnnouncementVO create(SystemAnnouncementVO systemAnnouncementVO) {
        logger.info("notify create system announcement,sendDate: {}", systemAnnouncementVO.getSendDate());

        //1.构建hzero的NoticeDTO并将数据插入数据库
        NoticeDTO noticeDTO = new NoticeDTO();
        // 标题
        noticeDTO.setTitle(systemAnnouncementVO.getTitle());
        // 内容
        noticeDTO.setNoticeBody(systemAnnouncementVO.getContent());
        // 置顶标志
        noticeDTO.setStickyFlag(Optional.ofNullable(systemAnnouncementVO.getSticky())
                .map(flag -> flag ? 1 : 0)
                .orElse(0));
        // 开始日期
        noticeDTO.setStartDate(Optional.ofNullable(systemAnnouncementVO.getSendDate())
                .map(date -> date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                .orElse(null));
        // 结束日志
        noticeDTO.setEndDate(Optional.ofNullable(systemAnnouncementVO.getEndDate())
                .map(date -> date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                .orElse(null));
        noticeDTO.setTenantId(TenantDTO.DEFAULT_TENANT_ID);
        noticeDTO.setLang(LANG);
        noticeDTO.setReceiverTypeCode(RECEIVER_TYPE_CODE_ANNOUNCE);
        noticeDTO.setNoticeTypeCode(NOTICE_TYPE_CODE_PTGG);
        noticeDTO.setStatusCode(Notice.STATUS_DRAFT);

        NoticeContent noticeContent = new NoticeContent();
        noticeContent.setNoticeBody(noticeDTO.getNoticeBody());
        noticeDTO.setNoticeContent(noticeContent);

        noticeDTO = noticeService.createNotice(noticeDTO);

        //2.调用hzero公告发布接口
        List<NoticeReceiver> noticeReceiverList = new ArrayList<>();
        noticeReceiverList.add(new NoticeReceiver()
                .setReceiverSourceId(TenantDTO.DEFAULT_TENANT_ID)
                .setReceiverTypeCode("ALL")
                .setTenantId(TenantDTO.DEFAULT_TENANT_ID));
        noticeReceiverService.createNoticeReceiver(noticeDTO.getNoticeId(), TenantDTO.DEFAULT_TENANT_ID, noticeReceiverList);

        Notice notice = noticeRepository.selectByPrimaryKey(noticeDTO.getNoticeId());

        systemAnnouncementVO.setId(notice.getNoticeId());
        systemAnnouncementVO.setStatus(notice.getStatusCode());
        systemAnnouncementVO.setObjectVersionNumber(notice.getObjectVersionNumber());
        return systemAnnouncementVO;
    }
//
//    @Override
//    public SystemAnnouncementDTO update(SystemAnnouncementDTO dto, ResourceLevel level, Long sourceId) {
//        //0.若系统公告已经完成
//        SystemAnnouncement originSA = announcementMapper.selectByPrimaryKey(dto.getId());
//        if (originSA == null) {
//            throw new CommonException("error.update.system.announcement.not.exist,id:" + dto.getId());
//        }
//        if (originSA.getStatus().equalsIgnoreCase(SystemAnnouncementDTO.AnnouncementStatus.COMPLETED.value())) {
//            throw new CommonException("error.update.systemAnnouncement.has.been.completed");
//        }
//        //1.创建新任务
//        SystemAnnouncement systemAnnouncement = modelMapper.map(dto, SystemAnnouncement.class);
//        Long scheduleTaskId = createScheduleTask(systemAnnouncement);
//        //2.更新系统公告
//        systemAnnouncement.setScheduleTaskId(scheduleTaskId);
//        if (announcementMapper.updateByPrimaryKeySelective(systemAnnouncement) != 1) {
//            //2.1.更新公告失败删除新建任务
//            asgardFeignClient.deleteSiteTaskByTaskId(scheduleTaskId);
//            throw new CommonException("error.systemAnnouncement.update");
//        }
//        //3.更新公告成功删除原任务
//        asgardFeignClient.deleteSiteTaskByTaskId(originSA.getScheduleTaskId());
//
//        return modelMapper.map(systemAnnouncement, SystemAnnouncementDTO.class);
//    }
//

    @Override
    public Page<SystemAnnouncementVO> pagingQuery(PageRequest pageRequest, String title, String status, String params) {
        return PageHelper.doPageAndSort(pageRequest, () -> announcementMapper.fulltextSearch(title, status, params));
    }

    //    @Override
//    public SystemAnnouncementDTO getDetailById(Long id) {
//        SystemAnnouncement systemAnnouncement = announcementMapper.selectByPrimaryKey(id);
//        if (systemAnnouncement == null) {
//            throw new CommonException("error.system.announcement.not.exist,id:" + id);
//        }
//        return modelMapper.map(systemAnnouncement, SystemAnnouncementDTO.class);
//    }
//
//
    @Override
    public void delete(Long id) {
        noticeService.deleteNotice(TenantDTO.DEFAULT_TENANT_ID, id);
    }
//
//    /**
//     * 系统公告JobTask
//     *
//     * @param map 参数map
//     */
//    @JobTask(maxRetryCount = 0, code = "systemNotification", params = {
//            @JobParam(name = "systemNocificationId", description = "系统公告Id", type = Long.class)
//    }, description = "平台层发送系统通知")
//    public void systemNotification(Map<String, Object> map) {
//        Long systemNocificationId = Optional.ofNullable((Long) map.get("systemNocificationId")).orElseThrow(() -> new CommonException("error.systemNotification.id.empty"));
//        sendSystemNotification(ResourceLevel.SITE, 0L, systemNocificationId);
//    }
//
//
//    @Override
//    public void sendSystemNotification(ResourceLevel sourceType, Long sourceId, Long systemNocificationId) {
//        //todo 此处只实现平台层系统公告 待实现组织/项目层公告
//        //根据公告id查询公告
//        SystemAnnouncement systemAnnouncement = announcementMapper.selectByPrimaryKey(systemNocificationId);
//        if (systemAnnouncement == null) {
//            throw new CommonException("error.systemAnnouncement.empty,id:" + systemNocificationId);
//        }
//        //如果系统公告“是否发送站内信”字段为“是”，则发送站内信
//        if (systemAnnouncement.getSendNotices()) {
//            //发送设置模板:平台层
//            String code = SITE_NOTYFICATION_CODE;
//            //发送对象：全体用户
//            List<Long> allUsersId = Arrays.asList(userFeignClient.getUserIds().getBody());
//            //设置发送内容
//            Map<String, Object> params = new HashMap<>();
//            params.put("title", systemAnnouncement.getTitle());
//            params.put("content", systemAnnouncement.getContent());
//            asyncSendAnnouncementUtils.sendNoticeToAll(null, allUsersId, code, params, sourceId);
//        }
//        //更新公告状态
//        systemAnnouncement.setStatus(SystemAnnouncementDTO.AnnouncementStatus.COMPLETED.value());
//        if (announcementMapper.updateByPrimaryKey(systemAnnouncement) != 1) {
//            throw new CommonException("error.systemAnnouncement.update");
//        }
//    }
//
//
//    @Override
//    public SystemAnnouncementDTO getLatestSticky() {
//        return announcementMapper.selectLastestSticky(new Date());
//    }
}
