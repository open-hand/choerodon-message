package io.choerodon.notify.api.service;

import io.choerodon.core.domain.Page;
import io.choerodon.notify.api.dto.EmailTemplateDTO;
import io.choerodon.notify.api.dto.EmailTemplateQueryDTO;

public interface EmailTemplateService {

    Page<EmailTemplateQueryDTO> page(EmailTemplateQueryDTO query);

    EmailTemplateDTO query(Long id);

    EmailTemplateDTO create(EmailTemplateDTO template);

    EmailTemplateDTO update(EmailTemplateDTO template);

}
