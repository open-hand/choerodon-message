package io.choerodon.message.infra.mapper;

import org.apache.ibatis.annotations.Param;
import org.hzero.message.domain.entity.TemplateServerLine;

/**
 * @author scp
 * @date 2020/5/10
 * @description
 */
public interface TemplateServerLineC7nMapper {

    TemplateServerLine queryByTempServerIdAndType(@Param("tempServerId") Long tempServerId,
                                                  @Param("type") String type);
}
