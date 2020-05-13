package io.choerodon.message.infra.mapper;


import io.choerodon.message.api.vo.SendSettingVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HzeroTemplateServerMapper {
    List<SendSettingVO> queryByCategoryCodeAndReceiveConfigFlag(@Param("level") String level, @Param("allowConfig") int allowConfig);
}
