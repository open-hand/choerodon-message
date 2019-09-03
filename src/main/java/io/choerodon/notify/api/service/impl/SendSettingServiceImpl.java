package io.choerodon.notify.api.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.core.exception.CommonException;
import io.choerodon.notify.api.dto.*;
import io.choerodon.notify.api.pojo.PmType;
import io.choerodon.notify.api.service.SendSettingService;
import io.choerodon.notify.api.validator.CommonValidator;
import io.choerodon.notify.domain.SendSetting;
import io.choerodon.notify.domain.Template;
import io.choerodon.notify.infra.mapper.SendSettingMapper;
import io.choerodon.notify.infra.mapper.TemplateMapper;
import io.choerodon.notify.infra.utils.ConvertUtils;
import io.choerodon.swagger.notify.NotifyBusinessTypeScanData;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SendSettingServiceImpl implements SendSettingService {

    public static final String SEND_SETTING_DOES_NOT_EXIST = "error.send.setting.not.exist";
    public static final String SEND_SETTING_UPDATE_EXCEPTION ="error.send.setting.update";
    private SendSettingMapper sendSettingMapper;
    private TemplateMapper templateMapper;
    private final ModelMapper modelMapper = new ModelMapper();

    public SendSettingServiceImpl(SendSettingMapper sendSettingMapper, TemplateMapper templateMapper) {
        this.sendSettingMapper = sendSettingMapper;
        this.templateMapper = templateMapper;
    }

    @Override
    public Set<BusinessTypeDTO> listNames(final String level) {
        SendSetting query = new SendSetting();
        query.setLevel(level);
        return sendSettingMapper.select(query).stream()
                .map(ConvertUtils::convertBusinessTypeDTO).collect(Collectors.toSet());
    }

    @Override
    public Set<BusinessTypeDTO> listNames() {
        return sendSettingMapper.selectAll().stream()
                .map(ConvertUtils::convertBusinessTypeDTO).collect(Collectors.toSet());
    }

    @Override
    public PageInfo<SendSettingListDTO> page(final String level, final String name, final String code,
                                             final String description, final String params, int page, int size) {
        return PageHelper.startPage(page, size).doSelectPageInfo(
                () -> sendSettingMapper.fulltextSearch(level, code, name, description, params));
    }

    @Override
    public PageInfo<SendSettingListDTO> page(final String name, final String code,
                                             final String description, final String params, int page, int size) {
        return PageHelper.startPage(page, size).doSelectPageInfo(
                () -> sendSettingMapper.fulltextSearch(null, code, name, description, params));
    }

    @Override
    public SendSetting update(SendSettingUpdateDTO updateDTO) {
        SendSetting db = sendSettingMapper.selectByPrimaryKey(updateDTO.getId());
        if (db == null) {
            throw new CommonException(SEND_SETTING_DOES_NOT_EXIST);
        }
        db.setObjectVersionNumber(updateDTO.getObjectVersionNumber());
        db.setEmailTemplateId(updateDTO.getEmailTemplateId());
        db.setSmsTemplateId(updateDTO.getSmsTemplateId());
        db.setPmTemplateId(updateDTO.getPmTemplateId());
        if (updateDTO.getRetryCount() != null) {
            db.setRetryCount(updateDTO.getRetryCount());
        }
        if (updateDTO.getIsManualRetry() != null) {
            db.setIsManualRetry(updateDTO.getIsManualRetry());
        }
        if (updateDTO.getIsSendInstantly() != null) {
            db.setIsSendInstantly(updateDTO.getIsSendInstantly());
        }
        if (PmType.NOTICE.getValue().equals(updateDTO.getPmType())) {
            db.setPmType(PmType.NOTICE.getValue());
        }
        if (PmType.MSG.getValue().equals(updateDTO.getPmType())) {
            db.setPmType(PmType.MSG.getValue());
        }
        if (updateDTO.getAllowConfig() != null) {
            db.setAllowConfig(updateDTO.getAllowConfig());
        }
        if (sendSettingMapper.updateByPrimaryKey(db) != 1) {
            throw new CommonException(SEND_SETTING_UPDATE_EXCEPTION);
        }
        return sendSettingMapper.selectByPrimaryKey(db.getId());
    }

    @Override
    public SendSettingDetailDTO query(Long id) {
        SendSettingDetailDTO sendSetting = sendSettingMapper.selectById(id);
        if (sendSetting == null) {
            throw new CommonException(SEND_SETTING_DOES_NOT_EXIST);
        }
        return sendSetting;
    }

    @Override
    public void createByScan(Set<NotifyBusinessTypeScanData> businessTypes) {
        businessTypes.stream().map(t -> modelMapper.map(t, SendSetting.class)).forEach(i -> {
            SendSetting query = sendSettingMapper.selectOne(new SendSetting(i.getCode()));
            if (query == null) {
                sendSettingMapper.insertSelective(i);
            } else {
                query.setName(i.getName());
                query.setDescription(i.getDescription());
                query.setLevel(i.getLevel());
                sendSettingMapper.updateByPrimaryKeySelective(query);
            }
        });
    }

    @Override
    public List<SendSettingDetailDTO> queryByLevelAndAllowConfig(String level, boolean allowConfig) {
        if (level != null) {
            CommonValidator.validatorLevel(level);
        }
        List<SendSettingDetailDTO> list = sendSettingMapper.queryByLevelAndAllowConfig(level, allowConfig);
        return list.stream().filter(s -> s.getEmailTemplateId() != null || s.getPmTemplateId() != null || s.getSmsTemplateId() != null)
                .collect(Collectors.toList());
    }


    @Override
    public void delete(Long id) {
        if (sendSettingMapper.selectByPrimaryKey(id) == null) {
            throw new CommonException(SEND_SETTING_DOES_NOT_EXIST);
        }
        sendSettingMapper.deleteByPrimaryKey(id);
    }

    @Override
    public PageInfo<MessageServiceVO> pagingAll(SendSetting filterDTO, String params, PageRequest pageRequest) {
        return PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize(), pageRequest.getSort().toSql()).doSelectPageInfo(
                () -> sendSettingMapper.doFTR(filterDTO, params));
    }


    @Override
    public MessageServiceVO enabled(Long id) {
        SendSetting enabledDTO = sendSettingMapper.selectByPrimaryKey(id);
        if (enabledDTO == null) {
            throw new CommonException(SEND_SETTING_DOES_NOT_EXIST);
        }
        if (!enabledDTO.getEnabled()) {
            enabledDTO.setEnabled(true);
            if (sendSettingMapper.updateByPrimaryKeySelective(enabledDTO) != 1) {
                throw new CommonException("error.send.setting.enabled");
            }
        }
        return getMessageServiceVO(enabledDTO);
    }

    @Override
    public MessageServiceVO disabled(Long id) {
        SendSetting disabledDTO = sendSettingMapper.selectByPrimaryKey(id);
        if (disabledDTO == null) {
            throw new CommonException(SEND_SETTING_DOES_NOT_EXIST);
        }
        if (disabledDTO.getEnabled()) {
            disabledDTO.setEnabled(false);
            if (sendSettingMapper.updateByPrimaryKeySelective(disabledDTO) != 1) {
                throw new CommonException("error.send.setting.disabled");
            }
        }
        return getMessageServiceVO(disabledDTO);
    }


    @Override
    public MessageServiceVO allowConfiguration(Long id) {
        SendSetting allowDTO = sendSettingMapper.selectByPrimaryKey(id);
        if (allowDTO == null) {
            throw new CommonException(SEND_SETTING_DOES_NOT_EXIST);
        }
        if (!allowDTO.getAllowConfig()) {
            allowDTO.setAllowConfig(true);
            if (sendSettingMapper.updateByPrimaryKeySelective(allowDTO) != 1) {
                throw new CommonException("error.send.setting.allow.configuration");
            }
        }
        return getMessageServiceVO(allowDTO);
    }

    @Override
    public MessageServiceVO forbiddenConfiguration(Long id) {
        SendSetting forbiddenDTO = sendSettingMapper.selectByPrimaryKey(id);
        if (forbiddenDTO == null) {
            throw new CommonException(SEND_SETTING_DOES_NOT_EXIST);
        }
        if (forbiddenDTO.getAllowConfig()) {
            forbiddenDTO.setAllowConfig(false);
            if (sendSettingMapper.updateByPrimaryKeySelective(forbiddenDTO) != 1) {
                throw new CommonException("error.send.setting.forbidden.configuration");
            }
        }
        return getMessageServiceVO(forbiddenDTO);
    }

    /**
     * 根据 notify_send_setting{@link SendSetting}
     * 获取 MessageServiceVO {@link MessageServiceVO}
     *
     * @param sendSetting {@link SendSetting}
     * @return {@link MessageServiceVO}
     */
    private MessageServiceVO getMessageServiceVO(SendSetting sendSetting) {
        return new MessageServiceVO()
                .setId(sendSetting.getId())
                .setMessageType(sendSetting.getName())
                .setIntroduce(sendSetting.getDescription())
                .setLevel(sendSetting.getLevel())
                .setAllowConfig(sendSetting.getAllowConfig())
                .setEnabled(sendSetting.getEnabled())
                .setObjectVersionNumber(sendSetting.getObjectVersionNumber());
    }


    @Override
    public EmailSendSettingVO getEmailSendSetting(Long id) {
        SendSetting sendSetting = sendSettingMapper.selectByPrimaryKey(id);
        if (sendSetting == null) {
            throw new CommonException(SEND_SETTING_DOES_NOT_EXIST);
        }
        Template emailTemplate = null;
        if (sendSetting.getEmailTemplateId() != null) {
            emailTemplate = templateMapper.selectByPrimaryKey(sendSetting.getEmailTemplateId());
        }
        return getEmailSendSettingVO(sendSetting, emailTemplate);
    }


    @Override
    public EmailSendSettingVO updateEmailSendSetting(EmailSendSettingVO updateVO) {
        SendSetting updateDTO = sendSettingMapper.selectByPrimaryKey(updateVO.getId());
        if (updateDTO == null) {
            throw new CommonException(SEND_SETTING_DOES_NOT_EXIST);
        }
        updateDTO.setRetryCount(updateVO.getRetryCount());
        updateDTO.setIsSendInstantly(updateVO.getSendInstantly());
        updateDTO.setIsManualRetry(updateVO.getManualRetry());
        updateDTO.setEmailTemplateId(updateVO.getEmailTemplateId());
        updateDTO.setObjectVersionNumber(updateVO.getObjectVersionNumber());
        if (sendSettingMapper.updateByPrimaryKeySelective(updateDTO) != 1) {
            throw new CommonException(SEND_SETTING_UPDATE_EXCEPTION);
        }
        return getEmailSendSetting(updateDTO.getId());
    }

    @Override
    public PmSendSettingVO getPmSendSetting(Long id) {
        SendSetting sendSetting = sendSettingMapper.selectByPrimaryKey(id);
        if (sendSetting == null) {
            throw new CommonException(SEND_SETTING_DOES_NOT_EXIST);
        }
        Template pmTemplate = null;
        if (sendSetting.getPmTemplateId() != null) {
            pmTemplate = templateMapper.selectByPrimaryKey(sendSetting.getPmTemplateId());
        }
        return getPmSendSettingVO(sendSetting, pmTemplate);
    }

    @Override
    public PmSendSettingVO updatePmSendSetting(PmSendSettingVO updateVO) {
        SendSetting updateDTO = sendSettingMapper.selectByPrimaryKey(updateVO.getId());
        if (updateDTO == null) {
            throw new CommonException(SEND_SETTING_DOES_NOT_EXIST);
        }
        updateDTO.setPmType(updateVO.getPmType());
        updateDTO.setPmTemplateId(updateVO.getPmTemplateId());
        updateDTO.setObjectVersionNumber(updateVO.getObjectVersionNumber());
        if (sendSettingMapper.updateByPrimaryKeySelective(updateDTO) != 1) {
            throw new CommonException(SEND_SETTING_UPDATE_EXCEPTION);
        }
        return getPmSendSetting(updateDTO.getId());
    }

    /**
     * 根据 notify_send_setting{@link SendSetting} 和 notify_template{@link Template}
     * 获取 EmailSendSettingVO {@link EmailSendSettingVO}
     *
     * @param sendSetting
     * @param template
     * @return
     */
    private EmailSendSettingVO getEmailSendSettingVO(SendSetting sendSetting, Template template) {
        EmailSendSettingVO emailSendSettingVO = new EmailSendSettingVO();
        BeanUtils.copyProperties(sendSetting, emailSendSettingVO);
        emailSendSettingVO.setSendInstantly(sendSetting.getIsSendInstantly());
        emailSendSettingVO.setManualRetry(sendSetting.getIsManualRetry());
        if (template != null) {
            emailSendSettingVO
                    .setEmailTemplateId(template.getId())
                    .setEmailTemplateTitle(template.getEmailTitle());
        }
        return emailSendSettingVO;
    }


    /**
     * 根据 notify_send_setting{@link SendSetting} 和 notify_template{@link Template}
     * 获取 PmSendSettingVO {@link PmSendSettingVO}
     *
     * @param sendSetting
     * @param template
     * @return
     */
    private PmSendSettingVO getPmSendSettingVO(SendSetting sendSetting, Template template) {
        PmSendSettingVO pmSendSettingVO = new PmSendSettingVO();
        BeanUtils.copyProperties(sendSetting, pmSendSettingVO);
        if (template != null) {
            pmSendSettingVO
                    .setPmTemplateId(template.getId())
                    .setPmTemplateName(template.getName())
                    .setPmTemplateTitle(template.getPmTitle());
        }
        return pmSendSettingVO;
    }
}
