package io.choerodon.notify.api.service;

import io.choerodon.core.domain.Page;
import io.choerodon.notify.api.dto.PmTemplateDTO;
import io.choerodon.notify.api.dto.TemplateNamesDTO;
import io.choerodon.notify.api.dto.TemplateQueryDTO;
import io.choerodon.notify.api.pojo.MessageType;
import io.choerodon.swagger.notify.EmailTemplateScanData;

import java.util.List;
import java.util.Set;

public interface PmTemplateService {

    Page<TemplateQueryDTO> pageByLevel(TemplateQueryDTO query, String level);

    List<TemplateNamesDTO> listNames(String level, String businessType);

    PmTemplateDTO query(Long id);

    PmTemplateDTO create(PmTemplateDTO template);

    PmTemplateDTO update(PmTemplateDTO template);

    void delete(Long id);

    void check(String code);

}
