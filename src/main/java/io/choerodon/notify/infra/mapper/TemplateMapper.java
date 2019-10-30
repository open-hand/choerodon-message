package io.choerodon.notify.infra.mapper;

import io.choerodon.notify.infra.dto.Template;
import org.apache.ibatis.annotations.Param;

import java.util.*;

import io.choerodon.mybatis.common.*;
import io.choerodon.notify.api.dto.*;
import io.choerodon.notify.api.query.*;

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
     */
    List<Template> pagedSearch(@Param("templateQuery") TemplateQuery templateQuery);


    /**
     * 全局检索，指定当前id排序在前.
     *
     * @param currentId 当前模板Id
     * @param params    模糊查询参数
     * @return 模板列表
     */
    List<TemplateVO> doFTR(@Param("businessType") String businessType,
                           @Param("messageType") String messageType,
                           @Param("name") String name,
                           @Param("predefined") Boolean predefined,
                           @Param("currentId") Long currentId,
                           @Param("params") String params);

}
