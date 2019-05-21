package io.choerodon.notify.api.service;

import io.choerodon.notify.api.dto.TemplateNamesDTO;

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
}
