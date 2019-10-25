package io.choerodon.notify.infra.mapper;

import io.choerodon.notify.infra.dto.SystemAnnouncement;
import org.apache.ibatis.annotations.Param;

import java.util.*;

import io.choerodon.mybatis.common.*;
import io.choerodon.notify.api.dto.*;

/**
 * @author dengyouquan
 **/
public interface SystemAnnouncementMapper extends Mapper<SystemAnnouncement> {
    List<SystemAnnouncementDTO> fulltextSearch(@Param("title") String title, @Param("status") String status, @Param("params") String params);

    SystemAnnouncementDTO selectLastestSticky(@Param("currentTime") Date currentTime);
}
