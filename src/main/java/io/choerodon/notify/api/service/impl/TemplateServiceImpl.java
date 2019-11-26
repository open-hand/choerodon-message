package io.choerodon.notify.api.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ext.NotExistedException;
import io.choerodon.notify.api.service.TemplateService;
import io.choerodon.notify.infra.dto.Template;
import io.choerodon.notify.infra.enums.SendingTypeEnum;
import io.choerodon.notify.infra.mapper.SendSettingMapper;
import io.choerodon.notify.infra.mapper.TemplateMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Optional;

@Component
public class TemplateServiceImpl implements TemplateService {

    private static final String TEMPLATE_DOES_NOT_EXIST = "error.template.not.exist";
    private static final String TEMPLATE_UPDATE_EXCEPTION = "error.template.update";

    private TemplateMapper templateMapper;

    private SendSettingMapper sendSettingMapper;


    public TemplateServiceImpl(TemplateMapper templateMapper, SendSettingMapper sendSettingMapper) {
        this.templateMapper = templateMapper;
        this.sendSettingMapper = sendSettingMapper;
    }

    @Override
    public Template createTemplate(Template templateDTO) {
        if (SendingTypeEnum.EMAIL.getValue().equals(templateDTO.getSendingType()) && templateDTO.getTitle() == null) {
            throw new CommonException("error.email.title.null");
        } else if (SendingTypeEnum.PM.getValue().equals(templateDTO.getSendingType()) && templateDTO.getTitle() == null) {
            throw new CommonException("error.pm.title.null");
        } else if (SendingTypeEnum.WH.getValue().equals(templateDTO.getSendingType()) && templateDTO.getTitle() == null) {
            throw new CommonException("error.webhook.title.null");
        }
        if (templateMapper.insertSelective(templateDTO) != 1) {
            throw new CommonException("error.template.insert");
        }
        return templateDTO;
    }

    @Override
    public Template updateTemplate(Template templateDTO) {
        Template template = templateMapper.selectByPrimaryKey(templateDTO.getId());
        if (template == null) {
            throw new CommonException(TEMPLATE_DOES_NOT_EXIST);
        }
        if (templateMapper.updateByPrimaryKeySelective(templateDTO) != 1) {
            throw new CommonException(TEMPLATE_UPDATE_EXCEPTION);
        }
        return templateDTO;
    }

    @Override
    public Template getOne(Template template) {
        //1.获取模版
        Template result = Optional.ofNullable(templateMapper.selectOne(template))
                .orElseThrow(() -> new NotExistedException("error.template.does.not.exist"));
        //2.校验模版内容不能为空
        if (ObjectUtils.isEmpty(result.getContent())) {
            throw new CommonException("error.template.content.empty");
        }
        //3.校验 邮件/站内信 模版标题不能为空
        if ((SendingTypeEnum.EMAIL.getValue().equalsIgnoreCase(result.getSendingType())
                || SendingTypeEnum.PM.getValue().equalsIgnoreCase(result.getSendingType()))
                && ObjectUtils.isEmpty(result.getTitle())) {
            throw new CommonException("error.template.title.empty");
        }
        //4.返回结果
        return result;
    }
}
