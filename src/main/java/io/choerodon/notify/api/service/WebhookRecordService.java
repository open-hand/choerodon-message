package io.choerodon.notify.api.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.notify.api.dto.WebhookRecordVO;
import org.springframework.data.domain.Pageable;

/**
 * @author jiameng.cao
 * @date 2019/11/4
 */
public interface WebhookRecordService {
    PageInfo<WebhookRecordVO> pagingWebHookRecord(Pageable pageable, WebhookRecordVO webhookRecordVO, String params);
}
