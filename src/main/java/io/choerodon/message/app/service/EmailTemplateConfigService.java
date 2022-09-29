package io.choerodon.message.app.service;

import io.choerodon.message.infra.dto.EmailTemplateConfigDTO;

/**
 * @author scp
 * @since 2022/09/26
 * 邮件模板配置service
 */
public interface EmailTemplateConfigService {
    /**
     * 根据组织id查询配置
     * @param tenantId
     * @return
     */
    EmailTemplateConfigDTO queryConfigByTenantId(Long tenantId);

    /**
     * 创建或更新配置
     * @param emailTemplateConfigDTO
     * @return
     */
    void createOrUpdateConfig(EmailTemplateConfigDTO emailTemplateConfigDTO);

}
