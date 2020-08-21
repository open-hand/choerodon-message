package io.choerodon.message.infra.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * Created by wangxiang on 2020/7/30
 */
public interface WebhookServerC7nMapper {

    /**
     * 项目下存不存在webhook配置
     * @param address
     * @param projectId
     * @return
     */
    Integer existWebHookUnderProject(@Param(value = "address") String address,@Param(value = "projectId") Long projectId);

    /**
     * 组织下是否存在webhook 配置
     * @param address
     * @param orgId
     * @return
     */
    Integer existWebHookUnderOrganization(@Param(value = "address") String address,@Param(value = "orgId") Long orgId);

}
