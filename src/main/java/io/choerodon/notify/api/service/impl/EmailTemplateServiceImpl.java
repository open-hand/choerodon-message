package io.choerodon.notify.api.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.notify.api.dto.EmailTemplateDTO;
import io.choerodon.notify.api.dto.EmailTemplateQueryDTO;
import io.choerodon.notify.api.dto.TemplateNamesDTO;
import io.choerodon.notify.api.service.EmailTemplateService;
import io.choerodon.notify.domain.Template;
import io.choerodon.notify.infra.mapper.TemplateMapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.choerodon.notify.domain.Template.MSG_TYPE_EMAIL;

@Service
public class EmailTemplateServiceImpl implements EmailTemplateService {

    private final TemplateMapper templateMapper;

    private final ModelMapper modelMapper = new ModelMapper();


    public EmailTemplateServiceImpl(TemplateMapper templateMapper) {
        this.templateMapper = templateMapper;
        modelMapper.addMappings(EmailTemplateDTO.entity2Dto());
        modelMapper.addMappings(EmailTemplateDTO.dto2Entity());
        modelMapper.addMappings(EmailTemplateQueryDTO.dto2Entity());
        modelMapper.addMappings(EmailTemplateQueryDTO.entity2Dto());
        modelMapper.validate();
    }

    @Override
    public Page<EmailTemplateQueryDTO> page(EmailTemplateQueryDTO query) {
        return PageHelper.doPageAndSort(query.getPageRequest(),
                () -> templateMapper.fulltextSearchEmail(query.getCode(), query.getName(),
                        query.getType(), query.getParams(), query.getIsPredefined()));

    }

    @Override
    public List<TemplateNamesDTO> listNames() {
        return templateMapper.selectNames();
    }

    @Override
    public EmailTemplateDTO query(Long id) {
        Template template = templateMapper.selectByPrimaryKey(id);
        if (template == null) {
            throw new CommonException("error.emailTemplate.notExist");
        }
        EmailTemplateDTO dto = modelMapper.map(template, EmailTemplateDTO.class);
        dto.setType(template.getBusinessType());
        return dto;
    }

    @Override
    public EmailTemplateDTO create(EmailTemplateDTO dto) {
        Template template = modelMapper.map(dto, Template.class);
        template.setMessageType(MSG_TYPE_EMAIL);
        template.setBusinessType(dto.getType());
        if (templateMapper.insertSelective(template) != 1) {
            throw new CommonException("error.emailTemplate.save");
        }
        EmailTemplateDTO returnDto = modelMapper.map(templateMapper.selectByPrimaryKey(template.getId()), EmailTemplateDTO.class);
        returnDto.setType(template.getBusinessType());
        return returnDto;
    }

    @Override
    public EmailTemplateDTO update(EmailTemplateDTO dto) {
        Template template = modelMapper.map(dto, Template.class);
        templateMapper.updateByPrimaryKeySelective(template);
        EmailTemplateDTO returnDto = modelMapper.map(templateMapper.selectByPrimaryKey(template.getId()), EmailTemplateDTO.class);
        returnDto.setType(template.getBusinessType());
        return returnDto;
    }
}
