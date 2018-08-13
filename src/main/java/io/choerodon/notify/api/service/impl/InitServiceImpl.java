package io.choerodon.notify.api.service.impl;

import io.choerodon.notify.domain.Config;
import io.choerodon.notify.domain.SendSetting;
import io.choerodon.notify.infra.config.NotifyProperties;
import io.choerodon.notify.infra.mapper.ConfigMapper;
import io.choerodon.notify.infra.mapper.SendSettingMapper;
import io.choerodon.notify.infra.utils.ConvertUtils;
import io.choerodon.notify.infra.utils.ValidatorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

@Service
@Slf4j
public class InitServiceImpl {

    private final ConfigMapper configMapper;

    private final SendSettingMapper sendSettingMapper;

    private final NotifyProperties notifyProperties;

    private final MailProperties mailProperties;

    public InitServiceImpl(ConfigMapper configMapper,
                           SendSettingMapper sendSettingMapper,
                           NotifyProperties notifyProperties,
                           MailProperties mailProperties) {
        this.configMapper = configMapper;
        this.sendSettingMapper = sendSettingMapper;
        this.notifyProperties = notifyProperties;
        this.mailProperties = mailProperties;
    }

    @PostConstruct
    public void initDb() {
        if (notifyProperties.isInitSpringEmailConfig()) {
            initEmailConfig();
        }
        initBusinessType();
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

    private void initBusinessType() {
        notifyProperties.getTypes().forEach((k, v) -> {
            try {
                if (sendSettingMapper.selectOne(new SendSetting(k)) == null) {
                    sendSettingMapper.insertSelective(new SendSetting(k, v.getName(), v.getDescription(), v.getLevel()));
                }
            } catch (Exception e) {
                log.warn("error.initService.initBusinessType {}", e);
            }
        });
    }

}
