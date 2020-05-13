package io.choerodon.message.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.message.api.vo.WebhookRecordVO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author scp
 * @date 2020/5/12
 * @description
 */
public interface WebhookRecordC7nService {
    Page<WebhookRecordVO> pagingWebHookRecord(PageRequest pageRequest, Long sourceId, Long webhookId, String status, String eventName, String type, String sourceLevel);

    WebhookRecordVO queryById(Long sourceId, Long recordId, String sourceLevel);
}
