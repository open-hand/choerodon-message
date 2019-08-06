package io.choerodon.notify.infra.mapper;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.notify.api.dto.EmailTemplateDTO;
import io.choerodon.notify.api.dto.PmTemplateDTO;
import io.choerodon.notify.api.dto.TemplateNamesDTO;
import io.choerodon.notify.api.query.TemplateQuery;
import io.choerodon.notify.api.dto.TemplateVO;
import io.choerodon.notify.domain.Template;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TemplateMapper extends Mapper<Template> {

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

    String selectLevelByCode(@Param("code") String code, @Param("messageType") String messageType);

    /**
     * 模糊查询短信模版
     * @param templateQuery
     * @return
     */
    List<Template> pagedSearch(@Param("templateQuery") TemplateQuery templateQuery);


    /**
     * 全局检索，指定当前id排序在前
     * @param filterDTO
     * @param currentId
     * @param params
     * @return
     */
    List<TemplateVO> doFTR(@Param("filterDTO") Template filterDTO,
                           @Param("currentId") Long currentId,
                           @Param("params") String[] params);
}
