package io.choerodon.message.app.service.impl;

import org.hzero.core.base.BaseAppService;
import org.hzero.message.domain.entity.DingTalkServer;
import org.hzero.message.domain.repository.DingTalkServerRepository;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.mybatis.helper.UniqueHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import io.choerodon.message.app.service.DingTalkServerC7nService;

@Service
public class DingTalkServerC7nServiceImpl extends BaseAppService implements DingTalkServerC7nService {
    @Autowired
    private DingTalkServerRepository dingTalkServerRepository;


    @Override
    public DingTalkServer updateDingTalkServer(Long organizationId, DingTalkServer dingTalkServer) {
        if (organizationId != null) {
            dingTalkServer.setTenantId(organizationId);
        }

        SecurityTokenHelper.validToken(dingTalkServer);
        this.validObject(dingTalkServer);
        dingTalkServer.validate();
        if (StringUtils.hasText(dingTalkServer.getAppSecret())) {
            DataSecurityHelper.open();
            dingTalkServer.setAppSecret(dingTalkServer.getAppSecret());
            this.dingTalkServerRepository.updateOptional(dingTalkServer, "serverName", "authType", "appKey", "appSecret", "authAddress", "agentId", "enabledFlag");
        } else {
            this.dingTalkServerRepository.updateOptional(dingTalkServer, "serverName", "authType", "appKey", "authAddress", "agentId", "enabledFlag");
        }

        this.dingTalkServerRepository.clearCache(dingTalkServer.getTenantId(), dingTalkServer.getServerCode());
        return dingTalkServer;
    }

    @Override
    public DingTalkServer addDingTalkServer(Long organizationId, DingTalkServer dingTalkServer) {
        dingTalkServer.setTenantId(organizationId);
        this.validObject(dingTalkServer);
        dingTalkServer.validate();
        Assert.isTrue(UniqueHelper.valid(dingTalkServer), "error.data_exists");
        dingTalkServer.setAppSecret(dingTalkServer.getAppSecret());
        DataSecurityHelper.open();
        this.dingTalkServerRepository.insertSelective(dingTalkServer);
        this.dingTalkServerRepository.clearCache(dingTalkServer.getTenantId(), dingTalkServer.getServerCode());
        return dingTalkServer;
    }
}
