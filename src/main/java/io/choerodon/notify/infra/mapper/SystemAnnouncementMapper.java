package io.choerodon.notify.infra.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.*;

import io.choerodon.mybatis.common.*;
import io.choerodon.notify.api.dto.*;
import io.choerodon.notify.domain.*;

/**
 * @author dengyouquan
 **/
public interface SystemAnnouncementMapper extends Mapper<SystemAnnouncement> {
    List<SystemAnnouncementDTO> fulltextSearch(@Param("title") String title, @Param("status") String status, @Param("param") String param);

    SystemAnnouncementDTO selectLastestSticky(@Param("currentTime") Date currentTime);
}
