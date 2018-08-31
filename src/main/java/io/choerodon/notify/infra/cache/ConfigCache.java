package io.choerodon.notify.infra.cache;

import io.choerodon.core.exception.FeignException;
import io.choerodon.notify.domain.Config;
import io.choerodon.notify.infra.mapper.ConfigMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ConfigCache {

    private final Config config = new Config();
    /**
     * 加入缓存超时，避免有人私自该数据库造成配置长时间未更新。
     */
    private long cacheTime = System.currentTimeMillis();

    private static final long CACHED_TIME = 600000L;

    private ConfigMapper configMapper;


    public ConfigCache(ConfigMapper configMapper) {
        this.configMapper = configMapper;
    }

    public Config getEmailConfig() {
        if (isEmailNotValid(config) || System.currentTimeMillis() - cacheTime > CACHED_TIME) {
           refreshConfig();
        }
        return config;
    }

    public void refreshConfig() {
        Config db = configMapper.selectOne(new Config());
        if (db == null) {
            throw new FeignException("error.noticeSend.emailConfigNotSet");
        }
        if (isEmailNotValid(db)) {
            throw new FeignException("error.noticeSend.emailConfigNotValid");
        }
        copyValue(db);
        cacheTime = System.currentTimeMillis();
    }

    private boolean isEmailNotValid(final Config config) {
        return StringUtils.isEmpty(config.getEmailAccount()) ||
                StringUtils.isEmpty(config.getEmailPassword()) ||
                StringUtils.isEmpty(config.getEmailHost()) ||
                StringUtils.isEmpty(config.getEmailProtocol()) ||
                config.getEmailPort() == null;
    }

    private void copyValue(final Config db) {
        config.setId(db.getId());
        config.setEmailPassword(db.getEmailPassword());
        config.setEmailAccount(db.getEmailAccount());
        config.setEmailProtocol(db.getEmailProtocol());
        config.setEmailHost(db.getEmailHost());
        config.setEmailPort(db.getEmailPort());
        config.setEmailSsl(db.getEmailSsl());
        config.setEmailSendName(db.getEmailSendName());
        config.setSmsKeyPassword(db.getSmsKeyPassword());
        config.setSmsDomain(db.getSmsDomain());
        config.setSmsKeyId(db.getSmsKeyId());
    }

}
