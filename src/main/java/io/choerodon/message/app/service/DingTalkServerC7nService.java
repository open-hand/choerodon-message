package io.choerodon.message.app.service;

import org.hzero.message.domain.entity.DingTalkServer;

public interface DingTalkServerC7nService {
    DingTalkServer updateDingTalkServer(Long organizationId, DingTalkServer dingTalkServer);

    DingTalkServer addDingTalkServer(Long organizationId, DingTalkServer dingTalkServer);
}
