package io.choerodon.notify.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.notify.api.dto.SystemAnnouncementDTO;
import io.choerodon.notify.domain.SystemAnnouncement;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dengyouquan
 **/
public interface SystemAnnouncementMapper extends BaseMapper<SystemAnnouncement> {
    List<SystemAnnouncementDTO> fulltextSearch(@Param("title") String title,
                                               @Param("content") String content,
                                               @Param("param") String param);
}
