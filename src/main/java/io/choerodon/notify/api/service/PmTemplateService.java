package io.choerodon.notify.api.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.notify.api.dto.PmTemplateDTO;
import io.choerodon.notify.api.dto.TemplateNamesDTO;
import io.choerodon.notify.api.dto.TemplateQueryDTO;

import java.util.List;

public interface PmTemplateService {

    PageInfo<TemplateQueryDTO> pageByLevel(TemplateQueryDTO query, String level, int page, int size);

    List<TemplateNamesDTO> listNames(String level, String businessType);

    PmTemplateDTO query(Long id);

    PmTemplateDTO create(PmTemplateDTO template);

    PmTemplateDTO update(PmTemplateDTO template);

    void check(String code);

}
