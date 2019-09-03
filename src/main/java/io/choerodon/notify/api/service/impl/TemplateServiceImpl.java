package io.choerodon.notify.api.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.core.exception.CommonException;
import io.choerodon.notify.api.dto.TemplateCreateVO;
import io.choerodon.notify.api.dto.TemplateVO;
import io.choerodon.notify.api.pojo.MessageType;
import io.choerodon.notify.api.service.TemplateService;
import io.choerodon.notify.domain.SendSetting;
import io.choerodon.notify.domain.Template;
import io.choerodon.notify.infra.mapper.SendSettingMapper;
import io.choerodon.notify.infra.mapper.TemplateMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static io.choerodon.notify.api.service.impl.SendSettingServiceImpl.SEND_SETTING_DOES_NOT_EXIST;

@Component
public class TemplateServiceImpl implements TemplateService {

    private static final String TEMPLATE_DOES_NOT_EXIST="error.template.not.exist";
    private static final String TEMPLATE_UPDATE_EXCEPTION="error.template.update";

    TemplateMapper templateMapper;

    SendSettingMapper sendSettingMapper;


    public TemplateServiceImpl(TemplateMapper templateMapper, SendSettingMapper sendSettingMapper) {
        this.templateMapper = templateMapper;
        this.sendSettingMapper = sendSettingMapper;
    }

    @Override
    public PageInfo<TemplateVO> pagingTemplateByMessageType(Template filterDTO, String[] params, PageRequest pageRequest) {
        Long currentId = getCurrentId(filterDTO.getBusinessType(), filterDTO.getMessageType());
        return PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize()).doSelectPageInfo(
                () -> templateMapper.doFTR(filterDTO, currentId, params));
    }

    @Override
    public TemplateVO getById(Long id) {
        Template template = templateMapper.selectByPrimaryKey(id);
        if (template == null) {
            throw new CommonException(TEMPLATE_DOES_NOT_EXIST);
        }
        TemplateVO resultVO = new TemplateVO();
        BeanUtils.copyProperties(template, resultVO);
        return resultVO.setPredefined(template.getIsPredefined());
    }


    @Override
    public Boolean deleteById(Long id) {
        // 1.校验存在
        Template template = templateMapper.selectByPrimaryKey(id);
        if (template == null) {
            throw new CommonException(TEMPLATE_DOES_NOT_EXIST);
        }
        // 2.不可删除预定义模版
        if (template.getIsPredefined()) {
            throw new CommonException("error.template.delete.predefined");
        }
        // 3. 不可删除发送设置的当前模版
        SendSetting sendSetting = new SendSetting();
        sendSetting.setCode(template.getBusinessType());
        sendSetting = sendSettingMapper.selectOne(sendSetting);
        if (sendSetting != null &&
                (id == sendSetting.getEmailTemplateId() || id == sendSetting.getPmTemplateId() || id == sendSetting.getSmsTemplateId())) {
            throw new CommonException("error.template.delete.current");
        }
        // 4.删除
        if (templateMapper.deleteByPrimaryKey(id) != 1) {
            throw new CommonException("error.template.delete");
        }
        return true;
    }

    @Override
    public Boolean setToTheCurrent(Long id) {
        //  1.校验存在
        Template template = templateMapper.selectByPrimaryKey(id);
        if (template == null) {
            throw new CommonException(TEMPLATE_DOES_NOT_EXIST);
        }
        // 2.获取发送设置
        SendSetting sendSetting = new SendSetting();
        sendSetting.setCode(template.getBusinessType());
        sendSetting = sendSettingMapper.selectOne(sendSetting);
        if (sendSetting == null) {
            throw new CommonException(SEND_SETTING_DOES_NOT_EXIST);
        }
        // 3.设为当前模版
        if (MessageType.EMAIL.getValue().equalsIgnoreCase(template.getMessageType())) {
            sendSetting.setEmailTemplateId(id);
        } else if (MessageType.PM.getValue().equalsIgnoreCase(template.getMessageType())) {
            sendSetting.setPmTemplateId(id);
        } else if (MessageType.SMS.getValue().equalsIgnoreCase(template.getMessageType())) {
            sendSetting.setSmsTemplateId(id);
        }
        if (sendSettingMapper.updateByPrimaryKeySelective(sendSetting) != 1) {
            throw new CommonException("error.send.setting.update");
        }
        return true;
    }

    @Override
    @Transactional
    public TemplateCreateVO createTemplate(Boolean setToTheCurrent, TemplateCreateVO createVO) {
        // 获取创建信息并创建模版
        Template createDTO = getTemplate(createVO);
        if (templateMapper.insertSelective(createDTO) != 1) {
            throw new CommonException("error.template.insert");
        }
        // 设为当前
        if (setToTheCurrent) {
            updateSendSettingTemplate(createDTO.getId(), createVO);
        }
        // 返回创建结果
        Template template = templateMapper.selectByPrimaryKey(createDTO.getId());
        BeanUtils.copyProperties(template, createVO);
        return createVO;
    }


    @Override
    public TemplateCreateVO updateTemplate(Boolean setToTheCurrent, TemplateCreateVO updateVO) {
        // 更新模版
        Template template = templateMapper.selectByPrimaryKey(updateVO.getId());
        if (template == null) {
            throw new CommonException(TEMPLATE_DOES_NOT_EXIST);
        }
        if (MessageType.EMAIL.getValue().equalsIgnoreCase(template.getMessageType())) {
            template.setEmailTitle(((TemplateCreateVO.EmailTemplateCreateVO) updateVO).getEmailTitle());
            template.setEmailContent(((TemplateCreateVO.EmailTemplateCreateVO) updateVO).getEmailContent());
        } else if (MessageType.PM.getValue().equalsIgnoreCase(template.getMessageType())) {
            template.setPmTitle(((TemplateCreateVO.PmTemplateCreateVO) updateVO).getPmTitle());
            template.setPmContent(((TemplateCreateVO.PmTemplateCreateVO) updateVO).getPmContent());
        } else if (MessageType.SMS.getValue().equalsIgnoreCase(template.getMessageType())) {
            template.setSmsContent(((TemplateCreateVO.SmsTemplateCreateVO) updateVO).getSmsContent());
        }
        if (templateMapper.updateByPrimaryKeySelective(template) != 1) {
            throw new CommonException(TEMPLATE_UPDATE_EXCEPTION);
        }
        // 设为当前
        if (setToTheCurrent) {
            updateSendSettingTemplate(template.getId(), updateVO);
        }
        // 返回创建结果
        template = templateMapper.selectByPrimaryKey(updateVO.getId());
        TemplateCreateVO resultVO = new TemplateCreateVO();
        BeanUtils.copyProperties(template, resultVO);
        return resultVO;
    }

    /**
     * 根据消息类型和触发类型
     * 获得发送设置中
     * 该类型的当前模版id
     *
     * @param messageType  消息类型
     * @param businessType 触发类型 即 发送设置code
     * @return
     */
    private Long getCurrentId(String businessType, String messageType) {
        SendSetting sendSetting = new SendSetting();
        sendSetting.setCode(businessType);
        sendSetting = sendSettingMapper.selectOne(sendSetting);
        if (sendSetting == null) {
            throw new CommonException(SEND_SETTING_DOES_NOT_EXIST);
        }
        if (MessageType.EMAIL.getValue().equalsIgnoreCase(messageType)) {
            return sendSetting.getEmailTemplateId();
        } else if (MessageType.PM.getValue().equalsIgnoreCase(messageType)) {
            return sendSetting.getPmTemplateId();
        } else if (MessageType.SMS.getValue().equalsIgnoreCase(messageType)) {
            return sendSetting.getSmsTemplateId();
        }
        return null;
    }

    /**
     * 根据 {@link TemplateCreateVO} 构造返回 {@link Template}
     *
     * @param createVO {@link TemplateCreateVO}
     * @return {@link Template}
     */
    private Template getTemplate(TemplateCreateVO createVO) {
        Template createDTO = new Template();
        createDTO.setIsPredefined(false);
        BeanUtils.copyProperties(createVO, createDTO);
        if (createVO instanceof TemplateCreateVO.EmailTemplateCreateVO) {
            createDTO.setMessageType(MessageType.EMAIL.getValue());
        } else if (createVO instanceof TemplateCreateVO.PmTemplateCreateVO) {
            createDTO.setMessageType(MessageType.PM.getValue());
        } else if (createVO instanceof TemplateCreateVO.SmsTemplateCreateVO) {
            createDTO.setMessageType(MessageType.SMS.getValue());
        }
        return createDTO;
    }

    /**
     * 更新发送设置的模版配置
     *
     * @param templateId 模版主键
     * @param createVO   更新的模版信息
     */
    private void updateSendSettingTemplate(Long templateId, TemplateCreateVO createVO) {
        SendSetting updateDTO = new SendSetting();
        updateDTO.setCode(createVO.getBusinessType());
        updateDTO = sendSettingMapper.selectOne(updateDTO);
        if (updateDTO == null) {
            throw new CommonException(SEND_SETTING_DOES_NOT_EXIST);
        }
        if (createVO instanceof TemplateCreateVO.EmailTemplateCreateVO) {
            updateDTO.setEmailTemplateId(templateId);
        } else if (createVO instanceof TemplateCreateVO.PmTemplateCreateVO) {
            updateDTO.setPmTemplateId(templateId);
            updateDTO.setPmType(((TemplateCreateVO.PmTemplateCreateVO) createVO).getPmType());
        } else if (createVO instanceof TemplateCreateVO.SmsTemplateCreateVO) {
            updateDTO.setSmsTemplateId(templateId);
        }
        if (sendSettingMapper.updateByPrimaryKeySelective(updateDTO) != 1) {
            throw new CommonException("error.send.setting.update");
        }
    }
}
