package io.choerodon.notify.infra.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.notify.api.dto.SystemAnnouncementDTO;
import io.choerodon.notify.api.vo.SystemNoticeSearchVO;
import io.choerodon.notify.domain.SystemAnnouncement;

/**
 * @author dengyouquan
 **/
public interface SystemAnnouncementMapper extends Mapper<SystemAnnouncement> {
    List<SystemAnnouncementDTO> fulltextSearch(@Param("searchVO") SystemNoticeSearchVO searchVO,
                                               @Param("param") String param);

    SystemAnnouncementDTO selectLastestSticky(@Param("currentTime") Date currentTime);
}
