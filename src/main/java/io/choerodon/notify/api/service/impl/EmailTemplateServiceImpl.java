package io.choerodon.notify.api.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.notify.api.dto.EmailTemplateDTO;
import io.choerodon.notify.api.dto.EmailTemplateQueryDTO;
import io.choerodon.notify.api.dto.TemplateNamesDTO;
import io.choerodon.notify.api.service.EmailTemplateService;
import io.choerodon.notify.domain.MessageType;
import io.choerodon.notify.domain.SendSetting;
import io.choerodon.notify.domain.Template;
import io.choerodon.notify.infra.mapper.SendSettingMapper;
import io.choerodon.notify.infra.mapper.TemplateMapper;
import io.choerodon.notify.infra.utils.ConvertUtils;
import io.choerodon.swagger.notify.EmailTemplateScanData;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class EmailTemplateServiceImpl implements EmailTemplateService {

    private final TemplateMapper templateMapper;

    private final SendSettingMapper sendSettingMapper;

    private final ModelMapper modelMapper = new ModelMapper();


    public EmailTemplateServiceImpl(TemplateMapper templateMapper,
                                    SendSettingMapper sendSettingMapper) {
        this.templateMapper = templateMapper;
        this.sendSettingMapper = sendSettingMapper;
        modelMapper.addMappings(EmailTemplateDTO.entity2Dto());
        modelMapper.addMappings(EmailTemplateDTO.dto2Entity());
        modelMapper.addMappings(EmailTemplateQueryDTO.dto2Entity());
        modelMapper.addMappings(EmailTemplateQueryDTO.entity2Dto());
        modelMapper.validate();
    }

    @Override
    public Page<EmailTemplateQueryDTO> pageByLevel(EmailTemplateQueryDTO query, String level) {
        return PageHelper.doPageAndSort(query.getPageRequest(),
                () -> templateMapper.fulltextSearchEmail(query.getCode(), query.getName(),
                        query.getType(), query.getParams(), query.getIsPredefined(), level));

    }

    @Override
    public List<TemplateNamesDTO> listNames(final String level) {
        return templateMapper.selectNamesByLevel(level);
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
        valid(dto.getType());
        Template template = modelMapper.map(dto, Template.class);
        template.setMessageType(MessageType.EMAIL.getValue());
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
        if (dto.getType() != null) {
            valid(dto.getType());
        }
        Template template = modelMapper.map(dto, Template.class);
        templateMapper.updateByPrimaryKeySelective(template);
        EmailTemplateDTO returnDto = modelMapper.map(templateMapper.selectByPrimaryKey(template.getId()), EmailTemplateDTO.class);
        returnDto.setType(template.getBusinessType());
        return returnDto;
    }

    @Override
    public void createByScan(Set<EmailTemplateScanData> set) {
        set.stream().map(ConvertUtils::convertEmailTemplate).forEach(t -> {
            Template query = templateMapper.selectOne(new Template(t.getCode(), t.getMessageType()));
            if (query == null) {
                templateMapper.insertSelective(t);
            } else {
                t.setId(query.getId());
                t.setObjectVersionNumber(query.getObjectVersionNumber());
                templateMapper.updateByPrimaryKeySelective(t);
            }
        });
    }

    private void valid(final String type) {
        if (sendSettingMapper.selectOne(new SendSetting(type)) == null) {
            throw new CommonException("error.emailTemplate.businessTypeNotExist");
        }
    }
}
