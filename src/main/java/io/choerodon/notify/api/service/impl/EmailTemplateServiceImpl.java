package io.choerodon.notify.api.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.choerodon.core.exception.CommonException;
import io.choerodon.notify.api.dto.EmailTemplateDTO;
import io.choerodon.notify.api.dto.TemplateNamesDTO;
import io.choerodon.notify.api.dto.TemplateQueryDTO;
import io.choerodon.notify.api.service.EmailTemplateService;
import io.choerodon.notify.infra.dto.SendSettingDTO;
import io.choerodon.notify.infra.dto.Template;
import io.choerodon.notify.infra.enums.SendingTypeEnum;
import io.choerodon.notify.infra.mapper.SendSettingMapper;
import io.choerodon.notify.infra.mapper.TemplateMapper;
import io.choerodon.notify.infra.utils.ConvertUtils;
import io.choerodon.swagger.notify.NotifyTemplateScanData;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;

import static io.choerodon.notify.infra.config.NotifyProperties.LEVEL_ORG;
import static io.choerodon.notify.infra.config.NotifyProperties.LEVEL_SITE;

@Service
public class EmailTemplateServiceImpl implements EmailTemplateService {

    private static final String ERROR_TEMPLATE_NOT_EXIST = "error.emailTemplate.notExist";

    private final TemplateMapper templateMapper;

    private final SendSettingMapper sendSettingMapper;

    private final ModelMapper modelMapper = new ModelMapper();


    public EmailTemplateServiceImpl(TemplateMapper templateMapper,
                                    SendSettingMapper sendSettingMapper) {
        this.templateMapper = templateMapper;
        this.sendSettingMapper = sendSettingMapper;
        //todo
//        modelMapper.addMappings(EmailTemplateDTO.entity2Dto());
//        modelMapper.addMappings(EmailTemplateDTO.dto2Entity());
//        modelMapper.addMappings(TemplateQueryDTO.dto2Entity());
//        modelMapper.addMappings(TemplateQueryDTO.entity2Dto());
        modelMapper.validate();
    }

    @Override
    public PageInfo<TemplateQueryDTO> pageByLevel(TemplateQueryDTO query, String level, int page, int size) {
        return PageHelper
                .startPage(page, size)
                .doSelectPageInfo(
                        () -> templateMapper
                                .fulltextSearchEmail(
                                        query.getCode(), query.getName(),
                                        query.getType(), query.getParams(),
                                        query.getIsPredefined(), level));
    }

    @Override
    public List<TemplateNamesDTO> listNames(final String level, final String businessType) {
        return templateMapper.selectNamesByLevelAndTypeAnyMessageType(level, businessType, SendingTypeEnum.EMAIL.getValue());
    }

    @Override
    public EmailTemplateDTO query(Long id) {
        Template template = templateMapper.selectByPrimaryKey(id);
        if (template == null || !template.getSendingType().equals(SendingTypeEnum.EMAIL.getValue())) {
            throw new CommonException(ERROR_TEMPLATE_NOT_EXIST);
        }
        EmailTemplateDTO dto = modelMapper.map(template, EmailTemplateDTO.class);
        dto.setType(template.getSendSettingCode());
        return dto;
    }

    @Override
    public EmailTemplateDTO create(EmailTemplateDTO dto) {
        valid(dto.getType());
        Template template = modelMapper.map(dto, Template.class);
        template.setSendingType(SendingTypeEnum.EMAIL.getValue());
        template.setSendSettingCode(dto.getType());
        if (templateMapper.insertSelective(template) != 1) {
            throw new CommonException("error.emailTemplate.save");
        }
        EmailTemplateDTO returnDto = modelMapper.map(templateMapper.selectByPrimaryKey(template.getId()), EmailTemplateDTO.class);
        returnDto.setType(template.getSendSettingCode());
        return returnDto;
    }

    @Override
    public EmailTemplateDTO update(EmailTemplateDTO dto) {
        Template dbTemplate = templateMapper.selectByPrimaryKey(dto.getId());
        if (dbTemplate == null || !dbTemplate.getSendingType().equals(SendingTypeEnum.EMAIL.getValue())) {
            throw new CommonException(ERROR_TEMPLATE_NOT_EXIST);
        }
        if (dto.getType() != null) {
            valid(dto.getType());
        }
        Template template = modelMapper.map(dto, Template.class);
        if (templateMapper.updateByPrimaryKeySelective(template) != 1) {
            throw new CommonException("error.emailTemplate.update");
        }
        EmailTemplateDTO returnDto = modelMapper.map(templateMapper.selectByPrimaryKey(template.getId()), EmailTemplateDTO.class);
        returnDto.setType(template.getSendSettingCode());
        return returnDto;
    }

    @Override
    public void createByScan(Set<NotifyTemplateScanData> set) {
        set.stream().map(ConvertUtils::convertNotifyTemplate).forEach(t -> {
            Template query = templateMapper.selectOne(new Template().setSendingType(t.getSendingType()).setSendSettingCode(t.getSendSettingCode()));
            Long templateId;
            if (query == null) {
                templateMapper.insertSelective(t);
                templateId = t.getId();
            } else {
                templateId = query.getId();
                t.setId(templateId);
                t.setObjectVersionNumber(query.getObjectVersionNumber());
                templateMapper.updateByPrimaryKeySelective(t);
            }
            SendSettingDTO sendSetting = sendSettingMapper.selectOne(new SendSettingDTO(t.getSendSettingCode()));
            //todo
//            if (sendSetting != null) {
//                if (NotifyType.EMAIL.getValue().equals(t.getSendingType()) && sendSetting.getEmailTemplateId() == null) {
//                    sendSetting.setEmailTemplateId(templateId);
//                } else if (NotifyType.PM.getValue().equals(t.getSendingType()) && sendSetting.getPmTemplateId() == null) {
//                    sendSetting.setPmTemplateId(templateId);
//                } else if (NotifyType.SMS.getValue().equals(t.getSendingType()) && sendSetting.getSmsTemplateId() == null) {
//                    sendSetting.setSmsTemplateId(templateId);
//                }
//                sendSettingMapper.updateByPrimaryKey(sendSetting);
//            }
        });

    }

    private void valid(final String type) {
        if (sendSettingMapper.selectOne(new SendSettingDTO(type)) == null) {
            throw new CommonException("error.emailTemplate.businessTypeNotExist");
        }
    }

    @Override
    public void delete(Long id) {
        Template dbTemplate = templateMapper.selectByPrimaryKey(id);
        if (dbTemplate == null || !dbTemplate.getSendingType().equals(SendingTypeEnum.EMAIL.getValue())) {
            throw new CommonException(ERROR_TEMPLATE_NOT_EXIST);
        }
        if (dbTemplate.getIsPredefined() == null || dbTemplate.getIsPredefined()) {
            throw new CommonException("error.emailTemplate.cannotDeletePredefined");
        } else {
            if (templateMapper.deleteByPrimaryKey(id) != 1) {
                throw new CommonException("error.emailTemplate.delete");
            }
        }
    }

    @Override
    public void check(String code) {
        String level = templateMapper.selectLevelByCode(code, SendingTypeEnum.EMAIL.getValue());
        if (StringUtils.isEmpty(level)) {
            return;
        }
        if (LEVEL_SITE.equals(level)) {
            throw new CommonException("error.emailTemplate.codeSiteExist");
        }
        if (LEVEL_ORG.equals(level)) {
            throw new CommonException("error.emailTemplate.codeOrgExist");
        }
    }
}
