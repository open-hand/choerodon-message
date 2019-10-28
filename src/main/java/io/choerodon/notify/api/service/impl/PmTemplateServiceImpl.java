package io.choerodon.notify.api.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.choerodon.core.exception.CommonException;
import io.choerodon.notify.api.dto.PmTemplateDTO;
import io.choerodon.notify.api.dto.TemplateNamesDTO;
import io.choerodon.notify.api.dto.TemplateQueryDTO;
import io.choerodon.notify.api.pojo.MessageType;
import io.choerodon.notify.api.service.PmTemplateService;
import io.choerodon.notify.infra.dto.SendSettingDTO;
import io.choerodon.notify.infra.dto.Template;
import io.choerodon.notify.infra.mapper.SendSettingMapper;
import io.choerodon.notify.infra.mapper.TemplateMapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

import static io.choerodon.notify.infra.config.NotifyProperties.LEVEL_ORG;
import static io.choerodon.notify.infra.config.NotifyProperties.LEVEL_SITE;

@Service
public class PmTemplateServiceImpl implements PmTemplateService {

    private static final String ERROR_TEMPLATE_NOT_EXIST = "error.pmTemplate.notExist";

    private final TemplateMapper templateMapper;

    private final SendSettingMapper sendSettingMapper;

    private final ModelMapper modelMapper = new ModelMapper();


    public PmTemplateServiceImpl(TemplateMapper templateMapper,
                                 SendSettingMapper sendSettingMapper) {
        this.templateMapper = templateMapper;
        this.sendSettingMapper = sendSettingMapper;
        modelMapper.addMappings(PmTemplateDTO.entity2Dto());
        modelMapper.addMappings(PmTemplateDTO.dto2Entity());
        modelMapper.addMappings(TemplateQueryDTO.dto2Entity());
        modelMapper.addMappings(TemplateQueryDTO.entity2Dto());
        modelMapper.validate();
    }

    @Override
    public PageInfo<TemplateQueryDTO> pageByLevel(TemplateQueryDTO query, String level, int page, int size) {
        return PageHelper
                .startPage(page, size)
                .doSelectPageInfo(
                        () -> templateMapper
                                .fulltextSearchStationLetter(
                                        query.getCode(), query.getName(),
                                        query.getType(), query.getParams(),
                                        query.getIsPredefined(), level));
    }

    @Override
    public List<TemplateNamesDTO> listNames(final String level, final String businessType) {
        return templateMapper.selectNamesByLevelAndTypeAnyMessageType(level, businessType, MessageType.PM.getValue());
    }

    @Override
    public PmTemplateDTO query(Long id) {
        Template template = templateMapper.selectByPrimaryKey(id);
        if (template == null || !template.getMessageType().equals(MessageType.PM.getValue())) {
            throw new CommonException(ERROR_TEMPLATE_NOT_EXIST);
        }
        PmTemplateDTO dto = modelMapper.map(template, PmTemplateDTO.class);
        dto.setType(template.getBusinessType());
        return dto;
    }

    @Override
    public PmTemplateDTO create(PmTemplateDTO dto) {
        valid(dto.getType());
        Template template = modelMapper.map(dto, Template.class);
        template.setMessageType(MessageType.PM.getValue());
        template.setBusinessType(dto.getType());
        if (templateMapper.insertSelective(template) != 1) {
            throw new CommonException("error.pmTemplate.save");
        }
        PmTemplateDTO returnDto = modelMapper.map(templateMapper.selectByPrimaryKey(template.getId()), PmTemplateDTO.class);
        returnDto.setType(template.getBusinessType());
        return returnDto;
    }

    @Override
    public PmTemplateDTO update(PmTemplateDTO dto) {
        Template dbTemplate = templateMapper.selectByPrimaryKey(dto.getId());
        if (dbTemplate == null || !dbTemplate.getMessageType().equals(MessageType.PM.getValue())) {
            throw new CommonException(ERROR_TEMPLATE_NOT_EXIST);
        }
        if (dto.getType() != null) {
            valid(dto.getType());
        }
        Template template = modelMapper.map(dto, Template.class);
        if (templateMapper.updateByPrimaryKeySelective(template) != 1) {
            throw new CommonException("error.pmTemplate.update");
        }
        PmTemplateDTO returnDto = modelMapper.map(templateMapper.selectByPrimaryKey(template.getId()), PmTemplateDTO.class);
        returnDto.setType(template.getBusinessType());
        return returnDto;
    }

    private void valid(final String type) {
        if (sendSettingMapper.selectOne(new SendSettingDTO(type)) == null) {
            throw new CommonException("error.pmTemplate.businessTypeNotExist");
        }
    }

    @Override
    public void delete(Long id) {
        Template dbTemplate = templateMapper.selectByPrimaryKey(id);
        if (dbTemplate == null || !dbTemplate.getMessageType().equals(MessageType.PM.getValue())) {
            throw new CommonException(ERROR_TEMPLATE_NOT_EXIST);
        }
        if (dbTemplate.getIsPredefined() == null || dbTemplate.getIsPredefined()) {
            throw new CommonException("error.pmTemplate.cannotDeletePredefined");
        } else {
            if (templateMapper.deleteByPrimaryKey(id) != 1) {
                throw new CommonException("error.pmTemplate.delete");
            }
        }
    }

    @Override
    public void check(String code) {
        String level = templateMapper.selectLevelByCode(code, MessageType.PM.getValue());
        if (StringUtils.isEmpty(level)) {
            return;
        }
        if (LEVEL_SITE.equals(level)) {
            throw new CommonException("error.pmTemplate.codeSiteExist");
        }
        if (LEVEL_ORG.equals(level)) {
            throw new CommonException("error.pmTemplate.codeOrgExist");
        }
    }
}
