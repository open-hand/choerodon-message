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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
        NoticeDTO noticeDTO = VOtoDTO(systemAnnouncementVO);

        noticeDTO = noticeService.createNotice(noticeDTO);

        //2.调用hzero公告发布接口
        List<NoticeReceiver> noticeReceiverList = new ArrayList<>();
        noticeReceiverList.add(new NoticeReceiver()
                .setReceiverSourceId(TenantDTO.DEFAULT_TENANT_ID)
                .setReceiverTypeCode("ALL")
                .setTenantId(TenantDTO.DEFAULT_TENANT_ID));
        noticeReceiverService.createNoticeReceiver(noticeDTO.getNoticeId(), TenantDTO.DEFAULT_TENANT_ID, noticeReceiverList);

        Notice notice = noticeRepository.selectByPrimaryKey(noticeDTO.getNoticeId());

        systemAnnouncementVO.setId(notice.getNoticeId())
                .setStatus(notice.getStatusCode())
                .setObjectVersionNumber(notice.getObjectVersionNumber());
        return systemAnnouncementVO;
    }

    @Override
    public SystemAnnouncementVO update(SystemAnnouncementVO systemAnnouncementVO) {

        NoticeDTO noticeDTO = VOtoDTO(systemAnnouncementVO);

        noticeDTO = noticeService.updateNotice(noticeDTO);

        return DTOtoVO(noticeDTO);

    }

    @Override
    public Page<SystemAnnouncementVO> pagingQuery(PageRequest pageRequest, String title, String status, String params) {
        return PageHelper.doPageAndSort(pageRequest, () -> systemAnnouncementMapper.fulltextSearch(title, status, params));
    }

    @Override
    public SystemAnnouncementVO getDetailById(Long id) {
        NoticeDTO noticeDTO = noticeRepository.detailNotice(TenantDTO.DEFAULT_TENANT_ID, id);
        return DTOtoVO(noticeDTO);
    }


    @Override
    public void delete(Long id) {
        noticeService.deleteNotice(TenantDTO.DEFAULT_TENANT_ID, id);
    }

    @Override
    public SystemAnnouncementVO getLatestSticky() {
        return systemAnnouncementMapper.selectLastestSticky(new Date());
    }

    private SystemAnnouncementVO DTOtoVO(NoticeDTO noticeDTO) {
        return new SystemAnnouncementVO()
                .setId(noticeDTO.getNoticeId())
                .setObjectVersionNumber(noticeDTO.getObjectVersionNumber())
                .setSendDate(noticeDTO.getPublishedDate())
                .setEndDate(Optional.ofNullable(noticeDTO.getEndDate())
                        .orElse(null))
                .setStatus(noticeDTO.getStatusCode())
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
}
