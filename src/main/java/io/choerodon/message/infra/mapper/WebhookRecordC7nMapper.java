package io.choerodon.message.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;


import io.choerodon.message.api.vo.WebhookRecordVO;

/**
 * @author scp
 * @date 2020/5/12
 * @description
 */
public interface WebhookRecordC7nMapper {
    List<WebhookRecordVO> pagingWebHookRecord(@Param("tenantId") Long tenantId,
                                              @Param("projectId") Long projectId,
                                              @Param("webhookId") Long webhookId,
                                              @Param("status") String status,
                                              @Param("eventName") String eventName,
                                              @Param("type") String type);

    WebhookRecordVO selectById(@Param("recordId") Long recordId);
}
