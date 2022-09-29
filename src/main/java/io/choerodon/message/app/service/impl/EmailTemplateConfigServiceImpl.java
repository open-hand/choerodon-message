package io.choerodon.message.app.service.impl;

import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import io.choerodon.message.app.service.EmailTemplateConfigService;
import io.choerodon.message.infra.dto.EmailTemplateConfigDTO;
import io.choerodon.message.infra.dto.iam.TenantDTO;
import io.choerodon.message.infra.mapper.EmailTemplateConfigMapper;
import io.choerodon.message.infra.utils.JsonHelper;

/**
 * @author scp
 * @since 2022/9/26
 */
@Service
public class EmailTemplateConfigServiceImpl implements EmailTemplateConfigService {
    public final static String EMAIL_CONFIG_CACHE_KEY_FORMAT = "hmsg:email:config:%s";

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private EmailTemplateConfigMapper emailTemplateConfigMapper;

    @Override
    public EmailTemplateConfigDTO queryConfigByTenantId(Long tenantId) {
        String cacheKey = String.format(EMAIL_CONFIG_CACHE_KEY_FORMAT, tenantId);
        String configJson = stringRedisTemplate.opsForValue().get(cacheKey);
        if (ObjectUtils.isEmpty(configJson)) {
            EmailTemplateConfigDTO configDTO = emailTemplateConfigMapper.selectByTenantId(tenantId);
            if (configDTO == null) {
                EmailTemplateConfigDTO defaultConfigDTO = emailTemplateConfigMapper.selectByTenantId(TenantDTO.DEFAULT_TENANT_ID);
                configDTO = new EmailTemplateConfigDTO();
                configDTO.setSlogan(defaultConfigDTO.getSlogan());
                configDTO.setLogo(defaultConfigDTO.getLogo());
                configDTO.setFooter(defaultConfigDTO.getFooter());
            }
            stringRedisTemplate.opsForValue().set(cacheKey, JsonHelper.marshalByJackson(configDTO), 1, TimeUnit.HOURS);
            return configDTO;
        } else {
            return JsonHelper.unmarshalByJackson(configJson, EmailTemplateConfigDTO.class);
        }
    }

    @Override
    public void createOrUpdateConfig(EmailTemplateConfigDTO emailTemplateConfigDTO) {
        EmailTemplateConfigDTO configDTO = emailTemplateConfigMapper.selectByTenantId(emailTemplateConfigDTO.getTenantId());
        if (configDTO == null) {
            emailTemplateConfigMapper.insertSelective(emailTemplateConfigDTO);
        } else {
            configDTO.setFooter(emailTemplateConfigDTO.getFooter());
            configDTO.setLogo(emailTemplateConfigDTO.getLogo());
            configDTO.setSlogan(emailTemplateConfigDTO.getSlogan());
            emailTemplateConfigMapper.updateByPrimaryKey(configDTO);
        }
        String cacheKey = String.format(EMAIL_CONFIG_CACHE_KEY_FORMAT, emailTemplateConfigDTO.getTenantId());
        stringRedisTemplate.delete(cacheKey);
    }
}
