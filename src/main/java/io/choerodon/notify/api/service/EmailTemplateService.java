package io.choerodon.notify.api.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.notify.api.dto.EmailTemplateDTO;
import io.choerodon.notify.api.dto.TemplateQueryDTO;
import io.choerodon.notify.api.dto.TemplateNamesDTO;
import io.choerodon.swagger.notify.NotifyTemplateScanData;

import java.util.List;
import java.util.Set;

public interface EmailTemplateService {

    PageInfo<TemplateQueryDTO> pageByLevel(TemplateQueryDTO query, String level, int page, int size);

    List<TemplateNamesDTO> listNames(String level, String businessType);

    EmailTemplateDTO query(Long id);

    EmailTemplateDTO create(EmailTemplateDTO template);

    EmailTemplateDTO update(EmailTemplateDTO template);

    void createByScan(Set<NotifyTemplateScanData> set);

    void delete(Long id);

    void check(String code);

}
