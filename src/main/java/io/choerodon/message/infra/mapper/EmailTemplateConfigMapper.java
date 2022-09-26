package io.choerodon.message.infra.mapper;

import org.apache.ibatis.annotations.Param;

import io.choerodon.message.infra.dto.EmailTemplateConfigDTO;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * @author scp
 * @date 2020/5/23
 * @description
 */
public interface EmailTemplateConfigMapper extends BaseMapper<EmailTemplateConfigDTO> {

    EmailTemplateConfigDTO selectByTenantId(@Param("tenantId") Long tenantId);
}
