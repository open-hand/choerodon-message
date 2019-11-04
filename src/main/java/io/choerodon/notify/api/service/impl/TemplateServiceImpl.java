package io.choerodon.notify.api.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ext.NotExistedException;
import io.choerodon.notify.api.dto.TemplateCreateVO;
import io.choerodon.notify.api.dto.TemplateVO;
import io.choerodon.notify.api.service.TemplateService;
import io.choerodon.notify.infra.dto.SendSettingDTO;
import io.choerodon.notify.infra.dto.Template;
import io.choerodon.notify.infra.enums.SendingTypeEnum;
import io.choerodon.notify.infra.mapper.SendSettingMapper;
import io.choerodon.notify.infra.mapper.TemplateMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Optional;

import static io.choerodon.notify.api.service.impl.SendSettingServiceImpl.SEND_SETTING_DOES_NOT_EXIST;

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
    public PageInfo<TemplateVO> pagingTemplateByMessageType(Pageable pageable, String businessType, String messageType, String name, Boolean predefined, String params) {
        Long currentId = getCurrentId(businessType, messageType);
        return PageHelper.startPage(pageable.getPageNumber(), pageable.getPageSize()).doSelectPageInfo(
                () -> templateMapper.doFTR(businessType, messageType, name, predefined, currentId, params));
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

//    @Override
//    @Transactional
//    public TemplateCreateVO createTemplate(Boolean setToTheCurrent, Template templateDTO) {
//        // 获取创建信息并创建模版
//        Template createDTO = getTemplate(createVO);
//        if (templateMapper.insertSelective(createDTO) != 1) {
//            throw new CommonException("error.template.insert");
//        }
//        // 设置当前模板
//        if (setToTheCurrent) {
//            updateSendSettingTemplate(createDTO.getId(), createVO, setToTheCurrent);
//        }
//        // 返回创建结果
//        Template template = templateMapper.selectByPrimaryKey(createDTO.getId());
//        BeanUtils.copyProperties(template, createVO);
//        return createVO;
//    }
//
//
//    @Override
//    public TemplateCreateVO updateTemplate(Boolean setToTheCurrent, Template templateDTO) {
//        // 更新模版
//        Template template = templateMapper.selectByPrimaryKey(updateVO.getId());
//        if (template == null) {
//            throw new CommonException(TEMPLATE_DOES_NOT_EXIST);
//        }
//        if (SendingTypeEnum.EMAIL.getValue().equalsIgnoreCase(template.getSendingType())) {
//            template.setTitle(((TemplateCreateVO.EmailTemplateCreateVO) updateVO).getEmailTitle());
//            template.setContent(((TemplateCreateVO.EmailTemplateCreateVO) updateVO).getEmailContent());
//        } else if (SendingTypeEnum.PM.getValue().equalsIgnoreCase(template.getSendingType())) {
//            template.setTitle(((TemplateCreateVO.PmTemplateCreateVO) updateVO).getPmTitle());
//            template.setContent(((TemplateCreateVO.PmTemplateCreateVO) updateVO).getPmContent());
//        } else if (SendingTypeEnum.SMS.getValue().equalsIgnoreCase(template.getSendingType())) {
//            template.setContent(((TemplateCreateVO.SmsTemplateCreateVO) updateVO).getSmsContent());
//        }
//        if (templateMapper.updateByPrimaryKeySelective(template) != 1) {
//            throw new CommonException(TEMPLATE_UPDATE_EXCEPTION);
//        }
//        // 设置或取消当前模板
//        updateSendSettingTemplate(template.getId(), updateVO, setToTheCurrent);
//        // 返回创建结果
//        template = templateMapper.selectByPrimaryKey(updateVO.getId());
//        TemplateCreateVO resultVO = new TemplateCreateVO();
//        BeanUtils.copyProperties(template, resultVO);
//        return resultVO;
//    }

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

    /**
     * 根据消息类型和触发类型
     * 获得发送设置中
     * 该类型的当前模版id
     *
     * @param messageType  消息类型
     * @param businessType 触发类型 即 发送设置code
     * @return 该类型的当前模版id
     */
    private Long getCurrentId(String businessType, String messageType) {
        SendSettingDTO sendSetting = new SendSettingDTO();
        sendSetting.setCode(businessType);
        sendSetting = sendSettingMapper.selectOne(sendSetting);
        if (sendSetting == null) {
            throw new CommonException(SEND_SETTING_DOES_NOT_EXIST);
        }
        //todo
//        if (SendingTypeEnum.EMAIL.getValue().equalsIgnoreCase(messageType)) {
//            return sendSetting.getEmailTemplateId();
//        } else if (SendingTypeEnum.PM.getValue().equalsIgnoreCase(messageType)) {
//            return sendSetting.getPmTemplateId();
//        } else if (SendingTypeEnum.SMS.getValue().equalsIgnoreCase(messageType)) {
//            return sendSetting.getSmsTemplateId();
//        }
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
            createDTO.setSendingType(SendingTypeEnum.EMAIL.getValue());
        } else if (createVO instanceof TemplateCreateVO.PmTemplateCreateVO) {
            createDTO.setSendingType(SendingTypeEnum.PM.getValue());
        } else if (createVO instanceof TemplateCreateVO.SmsTemplateCreateVO) {
            createDTO.setSendingType(SendingTypeEnum.SMS.getValue());
        }
        return createDTO;
    }

    /**
     * 更新发送设置的模版配置
     *
     * @param templateId 模版主键
     * @param createVO   更新的模版信息
     */
    private void updateSendSettingTemplate(Long templateId, TemplateCreateVO createVO, Boolean setToTheCurrent) {
        SendSettingDTO updateDTO = new SendSettingDTO();
        updateDTO.setCode(createVO.getBusinessType());
        updateDTO = sendSettingMapper.selectOne(updateDTO);
        if (updateDTO == null) {
            throw new CommonException(SEND_SETTING_DOES_NOT_EXIST);
        }
        //todo
//        if (createVO instanceof TemplateCreateVO.EmailTemplateCreateVO) {
//            // 设为当前模板
//            if (setToTheCurrent) {
//                updateDTO.setEmailTemplateId(templateId);
//            } else {
//                // 只有当前使用模板可取消
//                if (templateId.equals(updateDTO.getEmailTemplateId())) {
//                    updateDTO.setEmailTemplateId(null);
//                }
//            }
//        } else if (createVO instanceof TemplateCreateVO.PmTemplateCreateVO) {
//            // 设为当前模板
//            if (setToTheCurrent) {
//                updateDTO.setPmTemplateId(templateId);
//            } else {
//                // 只有当前使用模板可取消
//                if (templateId.equals(updateDTO.getPmTemplateId())) {
//                    updateDTO.setPmTemplateId(null);
//                }
//            }
//            updateDTO.setPmType(((TemplateCreateVO.PmTemplateCreateVO) createVO).getPmType());
//        } else if (createVO instanceof TemplateCreateVO.SmsTemplateCreateVO) {
//            // 设为当前模板
//            if (setToTheCurrent) {
//                updateDTO.setSmsTemplateId(templateId);
//            } else {
//                // 只有当前使用模板可取消
//                if (templateId.equals(updateDTO.getSmsTemplateId())) {
//                    updateDTO.setSmsTemplateId(null);
//                }
//            }
//        }
        if (sendSettingMapper.updateByPrimaryKey(updateDTO) != 1) {
            throw new CommonException("error.send.setting.update");
        }
    }


}
