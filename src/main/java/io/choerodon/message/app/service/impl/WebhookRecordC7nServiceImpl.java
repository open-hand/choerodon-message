package io.choerodon.message.app.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.message.api.vo.WebhookRecordVO;
import io.choerodon.message.app.service.WebhookRecordC7nService;
import io.choerodon.message.infra.feign.operator.IamClientOperator;
import io.choerodon.message.infra.mapper.WebhookRecordC7nMapper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author scp
 * @date 2020/5/12
 * @description
 */
@Service
public class WebhookRecordC7nServiceImpl implements WebhookRecordC7nService {
    @Autowired
    private WebhookRecordC7nMapper webhookRecordC7nMapper;
    @Autowired
    private IamClientOperator iamClientOperator;

    @Override
    public Page<WebhookRecordVO> pagingWebHookRecord(PageRequest pageRequest, Long sourceId, Long webhookId, String status, String eventName, String type, String sourceLevel) {
        if (sourceLevel.equals(ResourceLevel.PROJECT.value())) {
            Long tenantId = iamClientOperator.queryProjectById(sourceId).getOrganizationId();
            return PageHelper.doPageAndSort(pageRequest, () -> webhookRecordC7nMapper.pagingWebHookRecord(tenantId, sourceId, webhookId, status, eventName, type));
        } else {
            return PageHelper.doPageAndSort(pageRequest, () -> webhookRecordC7nMapper.pagingWebHookRecord(sourceId, null, webhookId, status, eventName, type));
        }
    }

    @Override
    public WebhookRecordVO queryById(Long sourceId, Long recordId, String sourceLevel) {
        return webhookRecordC7nMapper.selectById(sourceId, sourceLevel, recordId);
    }
}
