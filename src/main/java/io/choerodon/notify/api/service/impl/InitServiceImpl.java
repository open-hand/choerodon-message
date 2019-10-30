package io.choerodon.notify.api.service.impl;

import io.choerodon.notify.infra.dto.Config;
import io.choerodon.notify.infra.config.NotifyProperties;
import io.choerodon.notify.infra.mapper.ConfigMapper;
import io.choerodon.notify.infra.utils.ConvertUtils;
import io.choerodon.notify.infra.utils.ValidatorUtils;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

@Service
public class InitServiceImpl {

    private final ConfigMapper configMapper;

    private final NotifyProperties notifyProperties;

    private final MailProperties mailProperties;

    public InitServiceImpl(ConfigMapper configMapper,
                           NotifyProperties notifyProperties,
                           MailProperties mailProperties) {
        this.configMapper = configMapper;
        this.notifyProperties = notifyProperties;
        this.mailProperties = mailProperties;
    }

    @PostConstruct
    public void initDb() {
        if (notifyProperties.isInitSpringEmailConfig()) {
            initEmailConfig();
        }
    }

    private void initEmailConfig() {
        Config dbConfig = configMapper.selectOne(new Config());
        if (ValidatorUtils.valid(mailProperties)) {
            Config saveConfig = ConvertUtils.mailProperties2Config(mailProperties);
            if (dbConfig == null) {
                configMapper.insertSelective(saveConfig);
            } else if (StringUtils.isEmpty(dbConfig.getEmailAccount())) {
                saveConfig.setId(dbConfig.getId());
                saveConfig.setObjectVersionNumber(dbConfig.getObjectVersionNumber());
                configMapper.updateByPrimaryKeySelective(saveConfig);
            }
        }
    }

}
