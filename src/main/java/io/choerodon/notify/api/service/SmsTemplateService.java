package io.choerodon.notify.api.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.notify.api.dto.TemplateNamesDTO;
import io.choerodon.notify.api.query.TemplateQuery;
import io.choerodon.notify.infra.dto.Template;

import java.util.List;

/**
 * @author superlee
 * @since 2019-05-21
 */
public interface SmsTemplateService {

    /**
     * 根据层级和businessType查询模版名和code
     *
     * @param level
     * @param businessType
     * @return
     */
    List<TemplateNamesDTO> listNames(String level, String businessType);

    /**
     * 分页查询短信模版
     *
     * @param page
     * @param size
     * @param templateQuery
     * @return
     */
    PageInfo<Template> pagedSearch(int page, int size, TemplateQuery templateQuery);

    /**
     * 根据id查询模版
     *
     * @param id
     * @return
     */
    Template query(Long id);


    /**
     * 根据id更新模版
     *
     * @param id
     * @param templateDTO
     * @return
     */
    Template update(Long id, Template templateDTO);

    /**
     * 创建短信模版
     *
     * @param templateDTO
     * @return
     */
    Template create(Template templateDTO);

    /**
     * code重名校验
     *
     * @param code
     */
    void check(String code);
}
