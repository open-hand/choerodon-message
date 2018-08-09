package io.choerodon.notify.api.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.notify.api.dto.EmailConfigDTO;
import io.choerodon.notify.api.service.ConfigService;
import io.choerodon.notify.domain.Config;
import io.choerodon.notify.infra.mapper.ConfigMapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ConfigServiceImpl implements ConfigService {

    private final ConfigMapper configMapper;

    private final ModelMapper modelMapper = new ModelMapper();

    public ConfigServiceImpl(ConfigMapper configMapper) {
        this.configMapper = configMapper;
        modelMapper.addMappings(EmailConfigDTO.dto2Entity());
        modelMapper.addMappings(EmailConfigDTO.entity2Dto());
    }

    @Override
    public EmailConfigDTO save(EmailConfigDTO configDTO) {
        Config dtoConfig = modelMapper.map(configDTO, Config.class);
        Config dbConfig = configMapper.selectOne(new Config());

        if (StringUtils.isEmpty(dtoConfig)) {
            dtoConfig.setEmailSendName(dtoConfig.getEmailAccount());
        }
        if (dbConfig == null) {
            if (configMapper.insertSelective(dtoConfig) != 1) {
                throw new CommonException("error.config.save.insert");
            }
        } else {
            dtoConfig.setObjectVersionNumber(dbConfig.getObjectVersionNumber());
            dtoConfig.setId(dbConfig.getId());
            configMapper.updateByPrimaryKeySelective(dtoConfig);
        }
        return modelMapper.map(configMapper.selectByPrimaryKey(dtoConfig.getId()), EmailConfigDTO.class);
    }

}
