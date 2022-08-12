package io.choerodon.message.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.message.infra.dto.WebhookProjectRelDTO;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * @author scp
 * @date 2020/5/12
 * @description
 */
public interface WebhookProjectRelMapper extends BaseMapper<WebhookProjectRelDTO> {

    List<WebhookProjectRelDTO> selectByTenantId(@Param("tenantId") Long tenantId);

    List<WebhookProjectRelDTO> selectByProjectId(@Param("projectId") Long projectId);

}
