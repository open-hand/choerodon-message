package io.choerodon.notify.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.notify.api.dto.EmailTemplateDTO;
import io.choerodon.notify.api.dto.PmTemplateDTO;
import io.choerodon.notify.api.dto.TemplateNamesDTO;
import io.choerodon.notify.domain.Template;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TemplateMapper extends BaseMapper<Template> {

    List<PmTemplateDTO> fulltextSearchStationLetter(@Param("code") String code, @Param("name") String name,
                                                    @Param("type") String type, @Param("params") String params,
                                                    @Param("isPredefined") Boolean isPredefined,
                                                    @Param("level") String level);

    List<EmailTemplateDTO> fulltextSearchEmail(@Param("code") String code, @Param("name") String name,
                                               @Param("type") String type, @Param("params") String params,
                                               @Param("isPredefined") Boolean isPredefined,
                                               @Param("level") String level);

    List<TemplateNamesDTO> selectNamesByLevelAndTypeAnyMessageType(@Param("level") String level, @Param("type") String businessType,
                                                                   @Param("messageType") String messageType);

    List<TemplateNamesDTO> selectNamesByLevelAndType(@Param("level") String level, @Param("type") String businessType);

    String selectLevelByCode(@Param("code") String code);


}
