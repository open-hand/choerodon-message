package io.choerodon.notify.infra.mapper;

import java.util.Date;
import java.util.List;

import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import io.choerodon.notify.api.dto.SystemAnnouncementDTO;
import io.choerodon.notify.domain.SystemAnnouncement;

/**
 * @author dengyouquan
 **/
public interface SystemAnnouncementMapper extends Mapper<SystemAnnouncement> {
    List<SystemAnnouncementDTO> fulltextSearch(@Param("title") String title,
                                               @Param("content") String content,
                                               @Param("status") String status,
                                               @Param("isSendNotices") Boolean sendNotices,
                                               @Param("param") String param);

    SystemAnnouncementDTO selectLastestSticky(@Param("currentTime") Date currentTime);
}
