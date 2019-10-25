package io.choerodon.notify.api.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.choerodon.core.exception.CommonException;
import io.choerodon.notify.api.dto.TemplateNamesDTO;
import io.choerodon.notify.api.pojo.MessageType;
import io.choerodon.notify.api.query.TemplateQuery;
import io.choerodon.notify.api.service.SmsTemplateService;
import io.choerodon.notify.domain.SendSetting;
import io.choerodon.notify.infra.dto.Template;
import io.choerodon.notify.infra.mapper.SendSettingMapper;
import io.choerodon.notify.infra.mapper.TemplateMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

/**
 * @author superlee
 * @since 2019-05-21
 */
@Service
public class SmsTemplateServiceImpl implements SmsTemplateService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final TemplateMapper templateMapper;

    private final SendSettingMapper sendSettingMapper;

    public SmsTemplateServiceImpl(TemplateMapper templateMapper,
                                  SendSettingMapper sendSettingMapper) {
        this.templateMapper = templateMapper;
        this.sendSettingMapper = sendSettingMapper;
    }

    @Override
    public List<TemplateNamesDTO> listNames(String level, String businessType) {
        return templateMapper.selectNamesByLevelAndTypeAnyMessageType(level, businessType, MessageType.SMS.getValue());
    }


    @Override
    public PageInfo<Template> pagedSearch(int page, int size, TemplateQuery templateQuery) {
        return PageHelper.startPage(page, size).doSelectPageInfo(() -> templateMapper.pagedSearch(templateQuery));
    }

    @Override
    public Template query(Long id) {
        return templateMapper.selectByPrimaryKey(id);
    }

    @Override
    public Template update(Long id, Template templateDTO) {
        Template dto = templateMapper.selectByPrimaryKey(id);
        if (dto == null) {
            throw new CommonException("error.template.not.exist");
        }
        if (templateDTO.getObjectVersionNumber() == null) {
            throw new CommonException("error.template.objectVersionNumber.null");
        }
        validateBusinessType(templateDTO);

        validateContent(templateDTO);
        templateDTO.setMessageType("sms");

        if (dto.getIsPredefined()) {
            templateDTO.setCode(dto.getCode());
            templateDTO.setName(dto.getName());
            templateDTO.setIsPredefined(dto.getIsPredefined());
            templateDTO.setBusinessType(dto.getBusinessType());
        }

        if (templateMapper.updateByPrimaryKeySelective(templateDTO) != 1) {
            throw new CommonException("error.template.update");
        }
        return templateMapper.selectByPrimaryKey(id);
    }

    public void validateContent(Template templateDTO) {
        String content = templateDTO.getSmsContent();
        if (StringUtils.isEmpty(content)) {
            throw new CommonException("error.template.content.empty");
        }
        try {
            objectMapper.readTree(content);
        } catch (IOException e) {
            throw new CommonException("error.template.content.illegal.json");
        }
    }

    public void validateBusinessType(Template templateDTO) {
        String businessType = templateDTO.getBusinessType();
        SendSetting sendSetting = new SendSetting();
        sendSetting.setCode(businessType);
        if (sendSettingMapper.selectOne(sendSetting) == null) {
            throw new CommonException("error.template.illegal.businessType");
        }
    }

    @Override
    public Template create(Template templateDTO) {
        validateBusinessType(templateDTO);

        validateContent(templateDTO);
        templateDTO.setId(null);
        templateDTO.setMessageType("sms");
        templateDTO.setIsPredefined(false);
        templateMapper.insertSelective(templateDTO);

        return templateDTO;
    }

    @Override
    public void check(String code) {
        Template dto = new Template();
        dto.setCode(code);
        dto.setMessageType("sms");
        if (templateMapper.selectOne(dto) != null) {
            throw new CommonException("error.sms.template.code.exist");
        }
    }

    @Override
    public void delete(Long id) {
        Template dto = templateMapper.selectByPrimaryKey(id);
        if (dto == null) {
            return;
        }
        if (!"sms".equalsIgnoreCase(dto.getMessageType())) {
            throw new CommonException("error.not.sms.template");
        }
        if (dto.getIsPredefined()) {
            throw new CommonException("error.predefined.template.can.not.delete");
        }
        templateMapper.deleteByPrimaryKey(dto);
    }
}
