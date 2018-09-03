package io.choerodon.notify.api.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.notify.api.dto.EmailConfigDTO;
import io.choerodon.notify.api.service.ConfigService;
import io.choerodon.notify.domain.Config;
import io.choerodon.notify.infra.cache.ConfigCache;
import io.choerodon.notify.infra.mapper.ConfigMapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ConfigServiceImpl implements ConfigService {

    private final ConfigMapper configMapper;

    private final ModelMapper modelMapper = new ModelMapper();

    private final ConfigCache configCache;

    public ConfigServiceImpl(ConfigMapper configMapper, ConfigCache configCache) {
        this.configMapper = configMapper;
        this.configCache = configCache;
    }

    @Override
    public EmailConfigDTO create(EmailConfigDTO configDTO) {
        Config dtoConfig = modelMapper.map(configDTO, Config.class);
        Config dbConfig = configMapper.selectOne(new Config());

        if (StringUtils.isEmpty(dtoConfig)) {
            dtoConfig.setEmailSendName(dtoConfig.getEmailAccount());
        }
        if (dbConfig == null) {
            if (configMapper.insertSelective(dtoConfig) != 1) {
                throw new CommonException("error.emailConfig.save");
            }

        } else {
            throw new CommonException("error.emailConfig.exist");
        }
        configCache.refreshConfig();
        return modelMapper.map(configMapper.selectByPrimaryKey(dtoConfig.getId()), EmailConfigDTO.class);
    }

    @Override
    public EmailConfigDTO update(EmailConfigDTO configDTO) {
        Config dtoConfig = modelMapper.map(configDTO, Config.class);
        Config dbConfig = configMapper.selectOne(new Config());
        if (dbConfig == null) {
            throw new CommonException("error.emailConfig.notExist");
        }
        dtoConfig.setId(dbConfig.getId());
        if (configMapper.updateByPrimaryKeySelective(dtoConfig) != 1){
           throw new CommonException("error.emailConfig.update");
        }
        configCache.refreshConfig();
        return modelMapper.map(configMapper.selectByPrimaryKey(dtoConfig.getId()), EmailConfigDTO.class);
    }

    @Override
    public EmailConfigDTO selectEmail() {
        Config config = configMapper.selectOne(new Config());
        if (config == null) {
            throw new CommonException("error.emailConfig.notExist");
        }
        return modelMapper.map(config, EmailConfigDTO.class);
    }

}
