package io.choerodon.notify.api.service.impl;

import io.choerodon.notify.api.service.EmailTemplateService;
import io.choerodon.notify.infra.dto.Template;
import io.choerodon.notify.infra.mapper.TemplateMapper;
import io.choerodon.notify.infra.utils.ConvertUtils;
import io.choerodon.swagger.notify.NotifyTemplateScanData;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class EmailTemplateServiceImpl implements EmailTemplateService {
    private final TemplateMapper templateMapper;

    public EmailTemplateServiceImpl(TemplateMapper templateMapper) {
        this.templateMapper = templateMapper;
    }

    @Override
    public void createByScan(Set<NotifyTemplateScanData> set) {
        set.stream().map(ConvertUtils::convertNotifyTemplate).forEach(t -> {
            Template query = templateMapper.selectOne(new Template().setSendingType(t.getSendingType()).setSendSettingCode(t.getSendSettingCode()));
            Long templateId;
            if (query == null) {
                templateMapper.insertSelective(t);
            } else {
                if (query.getIsPredefined()) {
                    templateId = query.getId();
                    t.setId(templateId);
                    t.setObjectVersionNumber(query.getObjectVersionNumber());
                    templateMapper.updateByPrimaryKeySelective(t);
                }
            }
        });

    }
}
