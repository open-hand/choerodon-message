package io.choerodon.notify.api.service;

import io.choerodon.core.domain.Page;
import io.choerodon.notify.api.dto.EmailTemplateDTO;
import io.choerodon.notify.api.dto.EmailTemplateQueryDTO;
import io.choerodon.notify.api.dto.TemplateNamesDTO;

import java.util.List;

public interface EmailTemplateService {

    Page<EmailTemplateQueryDTO> pageByLevel(EmailTemplateQueryDTO query, String level);

    List<TemplateNamesDTO> listNames();

    EmailTemplateDTO query(Long id);

    EmailTemplateDTO create(EmailTemplateDTO template);

    EmailTemplateDTO update(EmailTemplateDTO template);

}
