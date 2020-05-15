package io.choerodon.message.infra.mapper;

import io.choerodon.message.api.vo.SystemAnnouncementVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;


/**
 * @author dengyouquan
 **/
public interface SystemAnnouncementMapper {
    List<SystemAnnouncementVO> fulltextSearch(@Param("title") String title, @Param("status") String status, @Param("params") String params);

    SystemAnnouncementVO selectLastestSticky(@Param("currentTime") Date currentTime);
}
