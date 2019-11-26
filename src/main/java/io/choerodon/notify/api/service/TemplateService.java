package io.choerodon.notify.api.service;

import io.choerodon.notify.infra.dto.Template;

/**
 * @author Eugen
 */
public interface TemplateService {

    /**
     * 创建模版
     *
     * @param template 创建信息
     * @return 创建结果
     */
    Template createTemplate(Template template);


    /**
     * 更新模版
     *
     * @param template 更新VO
     * @return 更新结果
     */
    Template updateTemplate(Template template);


    /**
     * 查询模版
     *
     * @param template
     * @return 模版详情
     */
    Template getOne(Template template);
}
