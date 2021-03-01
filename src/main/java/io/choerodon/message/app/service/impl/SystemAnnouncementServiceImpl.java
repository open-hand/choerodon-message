package io.choerodon.message.app.service.impl;


import io.choerodon.core.domain.Page;
import io.choerodon.message.api.vo.SystemAnnouncementVO;
import io.choerodon.message.app.service.SystemAnnouncementService;
import io.choerodon.message.infra.dto.iam.TenantDTO;
import io.choerodon.message.infra.mapper.SystemAnnouncementMapper;
import io.choerodon.message.infra.utils.PageInfoUtil;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.message.api.dto.NoticeDTO;
import org.hzero.message.app.service.NoticeReceiverService;
import org.hzero.message.app.service.NoticeService;
import org.hzero.message.domain.entity.Notice;
import org.hzero.message.domain.entity.NoticeContent;
import org.hzero.message.domain.entity.NoticeReceiver;
import org.hzero.message.domain.repository.NoticeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author dengyouquan
 **/
@Component
public class SystemAnnouncementServiceImpl implements SystemAnnouncementService {

    private static final Logger logger = LoggerFactory.getLogger(SystemAnnouncementServiceImpl.class);

    private static final String LANG = "zh_CN";
    private static final String RECEIVER_TYPE_CODE_ANNOUNCE = "ANNOUNCE";
    private static final String NOTICE_TYPE_CODE_PTGG = "PTGG";

    @Autowired
    private SystemAnnouncementMapper systemAnnouncementMapper;

    @Autowired
    private NoticeRepository noticeRepository;

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
        NoticeDTO noticeDTO = VOtoDTO(systemAnnouncementVO);

        noticeDTO = noticeService.createNotice(TenantDTO.DEFAULT_TENANT_ID, noticeDTO);

        //2.调用hzero公告发布接口
        List<NoticeReceiver> noticeReceiverList = new ArrayList<>();
        noticeReceiverList.add(new NoticeReceiver()
                .setReceiverSourceId(TenantDTO.DEFAULT_TENANT_ID)
                .setReceiverTypeCode("ALL")
                .setTenantId(TenantDTO.DEFAULT_TENANT_ID));
        noticeReceiverService.createNoticeReceiver(noticeDTO.getNoticeId(), TenantDTO.DEFAULT_TENANT_ID, noticeReceiverList);

        Notice notice = noticeRepository.selectByPrimaryKey(noticeDTO.getNoticeId());

        systemAnnouncementVO.setId(notice.getNoticeId())
                .setStatus(getTrueStatus(notice.getPublishedDate()))
                .setObjectVersionNumber(notice.getObjectVersionNumber());
        return systemAnnouncementVO;
    }

    @Override
    public SystemAnnouncementVO update(SystemAnnouncementVO systemAnnouncementVO) {

        NoticeDTO noticeDTO = VOtoDTO(systemAnnouncementVO);

        noticeDTO = noticeService.updateNotice(TenantDTO.DEFAULT_TENANT_ID, noticeDTO);

        return DTOtoVO(noticeDTO);

    }

    @Override
    public Page<SystemAnnouncementVO> pagingQuery(PageRequest pageRequest, String title, String status, String params) {
        Page<SystemAnnouncementVO> systemAnnouncementVOPage = PageHelper.doPageAndSort(pageRequest, () -> systemAnnouncementMapper.fulltextSearch(title, status, params));

        systemAnnouncementVOPage.getContent().forEach(
                systemAnnouncementVO -> {
                    systemAnnouncementVO.setStatus(getTrueStatus(systemAnnouncementVO.getSendDate()));
                }
        );
        if (!StringUtils.isEmpty(status)) {
            List<SystemAnnouncementVO> announcementVOList = systemAnnouncementVOPage.getContent().stream().filter(t -> t.getStatus().equals(status)).collect(Collectors.toList());
            systemAnnouncementVOPage.setContent(announcementVOList);
            return systemAnnouncementVOPage;
        } else {
            return systemAnnouncementVOPage;
        }
    }

    @Override
    public SystemAnnouncementVO getDetailById(Long id) {
        NoticeDTO noticeDTO = noticeRepository.detailNotice(TenantDTO.DEFAULT_TENANT_ID, id);
        return DTOtoVO(noticeDTO);
    }


    @Override
    public void delete(Long id) {
        NoticeDTO noticeDTO = noticeRepository.detailNotice(TenantDTO.DEFAULT_TENANT_ID, id);
        noticeService.deleteNotice(TenantDTO.DEFAULT_TENANT_ID, noticeDTO);
    }

    @Override
    public SystemAnnouncementVO getLatestSticky() {
        return systemAnnouncementMapper.selectLastestSticky(new Date());
    }

    private SystemAnnouncementVO DTOtoVO(NoticeDTO noticeDTO) {
        return new SystemAnnouncementVO()
                .setId(noticeDTO.getNoticeId())
                .setObjectVersionNumber(noticeDTO.getObjectVersionNumber())
                .setSendDate(noticeDTO.getStartDate())
                .setEndDate(Optional.ofNullable(noticeDTO.getEndDate())
                        .orElse(null))
                .setStatus(getTrueStatus(noticeDTO.getPublishedDate()))
                .setContent(noticeDTO.getNoticeContent().getNoticeBody())
                .setTitle(noticeDTO.getTitle())
                .setSticky(Optional.ofNullable(noticeDTO.getStickyFlag())
                        .map(flag -> flag.equals(1))
                        .orElse(null)
                );
    }

    private NoticeDTO VOtoDTO(SystemAnnouncementVO systemAnnouncementVO) {
        NoticeDTO noticeDTO = new NoticeDTO();
        noticeDTO.setTitle(systemAnnouncementVO.getTitle())
                .setNoticeBody(systemAnnouncementVO.getContent())
                .setStickyFlag(Optional.ofNullable(systemAnnouncementVO.getSticky())
                        .map(flag -> flag ? 1 : 0)
                        .orElse(0))
                .setStartDate(Optional.ofNullable(systemAnnouncementVO.getSendDate())
                        .orElse(null))
                .setEndDate(Optional.ofNullable(systemAnnouncementVO.getEndDate())
                        .orElse(null))
                .setTenantId(TenantDTO.DEFAULT_TENANT_ID)
                .setLang(LANG)
                .setReceiverTypeCode(RECEIVER_TYPE_CODE_ANNOUNCE)
                .setNoticeTypeCode(NOTICE_TYPE_CODE_PTGG)
                .setStatusCode(Notice.STATUS_DRAFT);


        NoticeContent noticeContent = new NoticeContent()
                .setNoticeBody(noticeDTO.getNoticeBody());
        noticeDTO.setNoticeContent(noticeContent);

        return noticeDTO;
    }

    /**
     * 直接从数据库取出的status字段不满足实际需求，因此根据发送时间与系统当前时间比较来获取状态
     */
    private String getTrueStatus(Date publishDate) {
        if (publishDate.getTime() < System.currentTimeMillis()) {
            return Notice.STATUS_PUBLISHED;
        } else {
            return "WAITING";
        }
    }
}
