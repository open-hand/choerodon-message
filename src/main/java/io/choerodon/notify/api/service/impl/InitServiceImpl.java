package io.choerodon.notify.api.service.impl;

import io.choerodon.notify.domain.BusinessType;
import io.choerodon.notify.domain.Config;
import io.choerodon.notify.infra.config.NotifyProperties;
import io.choerodon.notify.infra.mapper.BusinessTypeMapper;
import io.choerodon.notify.infra.mapper.ConfigMapper;
import io.choerodon.notify.infra.utils.ValidatorUtils;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

@Service
@Slf4j
public class InitServiceImpl {

    private final ConfigMapper configMapper;

    private final BusinessTypeMapper businessTypeMapper;

    private final NotifyProperties notifyProperties;

    private final ModelMapper modelMapper = new ModelMapper();

    public InitServiceImpl(ConfigMapper configMapper,
                           BusinessTypeMapper businessTypeMapper,
                           NotifyProperties notifyProperties) {
        this.configMapper = configMapper;
        this.businessTypeMapper = businessTypeMapper;
        this.notifyProperties = notifyProperties;
        modelMapper.addMappings(NotifyProperties.Email.properties2Entity());
    }

    @PostConstruct
    public void initDb() {
        if (notifyProperties.isInitEmailConfig()) {
            initEmailConfig();
        }
        initBusinessType();
    }

    private void initEmailConfig() {
        Config dbConfig = configMapper.selectOne(new Config());
        if (ValidatorUtils.valid(notifyProperties.getEmail())) {
            Config saveConfig = modelMapper.map(notifyProperties.getEmail(), Config.class);
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
        notifyProperties.getBusinessType().forEach((k, v) -> {
            try {
                if (businessTypeMapper.selectOne(new BusinessType(k)) == null) {
                    businessTypeMapper.insertSelective(new BusinessType(k, v));
                }
            } catch (Exception e) {
                log.warn("error.initService.initBusinessType {}", e);
            }
        });
    }

}
