package io.choerodon.notify.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.notify.api.dto.SystemAnnouncementDTO;
import io.choerodon.notify.domain.SystemAnnouncement;

/**
 * @author dengyouquan
 **/
public interface SystemAnnouncementMapper extends BaseMapper<SystemAnnouncement> {
    List<SystemAnnouncementDTO> fulltextSearch(@Param("title") String title,
                                               @Param("content") String content,
                                               @Param("status") String status,
                                               @Param("isSendNotices") Boolean sendNotices,
                                               @Param("param") String param);

    SystemAnnouncement selectLastestSticky();
}
