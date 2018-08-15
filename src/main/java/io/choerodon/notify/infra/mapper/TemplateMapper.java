package io.choerodon.notify.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.notify.api.dto.EmailTemplateDTO;
import io.choerodon.notify.api.dto.TemplateNamesDTO;
import io.choerodon.notify.domain.Template;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TemplateMapper extends BaseMapper<Template> {

    List<EmailTemplateDTO> fulltextSearchEmail(@Param("code") String code, @Param("name") String name,
                                               @Param("type") String type, @Param("params") String params,
                                               @Param("isPredefined") Boolean isPredefined,
                                               @Param("level") String level);


    List<TemplateNamesDTO> selectNamesByLevel(@Param("level") String level);


}
