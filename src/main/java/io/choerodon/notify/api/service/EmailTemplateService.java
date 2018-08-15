package io.choerodon.notify.api.service;

import io.choerodon.core.domain.Page;
import io.choerodon.notify.api.dto.EmailTemplateDTO;
import io.choerodon.notify.api.dto.EmailTemplateQueryDTO;
import io.choerodon.notify.api.dto.TemplateNamesDTO;
import io.choerodon.swagger.notify.EmailTemplateScanData;

import java.util.List;
import java.util.Set;

public interface EmailTemplateService {

    Page<EmailTemplateQueryDTO> pageByLevel(EmailTemplateQueryDTO query, String level);

    List<TemplateNamesDTO> listNames(String level);

    EmailTemplateDTO query(Long id);

    EmailTemplateDTO create(EmailTemplateDTO template);

    EmailTemplateDTO update(EmailTemplateDTO template);

    void createByScan(Set<EmailTemplateScanData> set);

}
