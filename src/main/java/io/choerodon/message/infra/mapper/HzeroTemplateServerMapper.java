package io.choerodon.message.infra.mapper;


import io.choerodon.message.infra.dto.TemplateServerC7NDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HzeroTemplateServerMapper {
    List<TemplateServerC7NDTO> queryByCategoryCodeAndReceiveConfigFlag(@Param("level") String level, @Param("allowConfig") int allowConfig);
}
