package io.choerodon.notify.api.service.impl;

import io.choerodon.notify.api.dto.TemplateNamesDTO;
import io.choerodon.notify.api.pojo.MessageType;
import io.choerodon.notify.api.service.SmsTemplateService;
import io.choerodon.notify.infra.mapper.TemplateMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author superlee
 * @since 2019-05-21
 */
@Service
public class SmsTemplateServiceImpl implements SmsTemplateService {

    private final TemplateMapper templateMapper;

    public SmsTemplateServiceImpl(TemplateMapper templateMapper) {
        this.templateMapper = templateMapper;
    }

    @Override
    public List<TemplateNamesDTO> listNames(String level, String businessType) {
        return templateMapper.selectNamesByLevelAndTypeAnyMessageType(level, businessType, MessageType.SMS.getValue());
    }
}
