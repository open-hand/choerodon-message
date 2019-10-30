package io.choerodon.notify.api.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.notify.api.dto.*;
import io.choerodon.notify.api.pojo.PmType;
import io.choerodon.notify.api.service.SendSettingService;
import io.choerodon.notify.api.validator.CommonValidator;
import io.choerodon.notify.api.vo.WebHookVO;
import io.choerodon.notify.infra.dto.SendSettingCategoryDTO;
import io.choerodon.notify.infra.dto.SendSettingDTO;
import io.choerodon.notify.infra.dto.Template;
import io.choerodon.notify.infra.enums.LevelType;
import io.choerodon.notify.infra.mapper.SendSettingCategoryMapper;
import io.choerodon.notify.infra.mapper.SendSettingMapper;
import io.choerodon.notify.infra.mapper.TemplateMapper;
import io.choerodon.notify.infra.utils.ConvertUtils;
import io.choerodon.swagger.notify.NotifyBusinessTypeScanData;
import io.choerodon.web.util.PageableHelper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SendSettingServiceImpl implements SendSettingService {

    public static final String SEND_SETTING_DOES_NOT_EXIST = "error.send.setting.not.exist";
    public static final String SEND_SETTING_UPDATE_EXCEPTION = "error.send.setting.update";
    private SendSettingMapper sendSettingMapper;
    private SendSettingCategoryMapper sendSettingCategoryMapper;
    private TemplateMapper templateMapper;
    private final ModelMapper modelMapper = new ModelMapper();

    public SendSettingServiceImpl(SendSettingMapper sendSettingMapper, SendSettingCategoryMapper sendSettingCategoryMapper, TemplateMapper templateMapper) {
        this.sendSettingMapper = sendSettingMapper;
        this.sendSettingCategoryMapper = sendSettingCategoryMapper;
        this.templateMapper = templateMapper;
    }

    @Override
    public Set<BusinessTypeDTO> listNames(final String level) {
        SendSettingDTO query = new SendSettingDTO();
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
    public SendSettingDTO update(SendSettingUpdateDTO updateDTO) {
        SendSettingDTO db = sendSettingMapper.selectByPrimaryKey(updateDTO.getId());
        if (db == null) {
            throw new CommonException(SEND_SETTING_DOES_NOT_EXIST);
        }
        db.setObjectVersionNumber(updateDTO.getObjectVersionNumber());
        //todo
//        db.setEmailTemplateId(updateDTO.getEmailTemplateId());
//        db.setSmsTemplateId(updateDTO.getSmsTemplateId());
//        db.setPmTemplateId(updateDTO.getPmTemplateId());
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
            db.setBacklogFlag(true);
        }
        if (PmType.MSG.getValue().equals(updateDTO.getPmType())) {
            db.setBacklogFlag(false);
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
    public SendSettingVO query(String code) {
        SendSettingDTO sendSettingDTO = new SendSettingDTO();
        sendSettingDTO.setCode(code);
        sendSettingDTO = sendSettingMapper.selectOne(sendSettingDTO);
        if (sendSettingDTO == null) {
            throw new CommonException(SEND_SETTING_DOES_NOT_EXIST);
        }
        SendSettingVO sendSetting = new SendSettingVO();
        BeanUtils.copyProperties(sendSettingDTO, sendSetting);
        Template template = new Template();
        template.setSendSettingCode(code);
        sendSetting.setTemplates(templateMapper.select(template));
        return sendSetting;
    }

    @Override
    public void createByScan(Set<NotifyBusinessTypeScanData> businessTypes) {
        businessTypes.stream().map(t -> modelMapper.map(t, SendSettingDTO.class)).forEach(i -> {
            SendSettingDTO query = sendSettingMapper.selectOne(new SendSettingDTO(i.getCode()));
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
    public PageInfo<MessageServiceVO> pagingAll(String messageType, String introduce, Boolean enabled, Boolean allowConfig, String params, Pageable pageable, String firstCode, String secondCode) {
        return PageHelper.startPage(pageable.getPageNumber(), pageable.getPageSize(), PageableHelper.getSortSql(pageable.getSort())).doSelectPageInfo(
                () -> sendSettingMapper.doFTR(messageType, introduce, firstCode, secondCode, enabled, allowConfig, params));
    }


    @Override
    public MessageServiceVO enabled(String code) {
        SendSettingDTO enabledDTO = new SendSettingDTO();
        enabledDTO.setCode(code);
        enabledDTO = sendSettingMapper.selectOne(enabledDTO);
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
    public MessageServiceVO disabled(String code) {
        SendSettingDTO disabledDTO = new SendSettingDTO();
        disabledDTO.setCode(code);
        disabledDTO = sendSettingMapper.selectOne(disabledDTO);
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
        SendSettingDTO allowDTO = sendSettingMapper.selectByPrimaryKey(id);
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
        SendSettingDTO forbiddenDTO = sendSettingMapper.selectByPrimaryKey(id);
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
     * 根据 notify_send_setting{@link SendSettingDTO}
     * 获取 MessageServiceVO {@link MessageServiceVO}
     *
     * @param sendSetting {@link SendSettingDTO}
     * @return {@link MessageServiceVO}
     */
    private MessageServiceVO getMessageServiceVO(SendSettingDTO sendSetting) {
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
        SendSettingDTO sendSetting = sendSettingMapper.selectByPrimaryKey(id);
        if (sendSetting == null) {
            throw new CommonException(SEND_SETTING_DOES_NOT_EXIST);
        }
        Template emailTemplate = null;
        //todo
//        if (sendSetting.getEmailTemplateId() != null) {
//            emailTemplate = templateMapper.selectByPrimaryKey(sendSetting.getEmailTemplateId());
//        }
        return getEmailSendSettingVO(sendSetting, emailTemplate);
    }


    @Override
    public PmSendSettingVO getPmSendSetting(Long id) {
        SendSettingDTO sendSetting = sendSettingMapper.selectByPrimaryKey(id);
        if (sendSetting == null) {
            throw new CommonException(SEND_SETTING_DOES_NOT_EXIST);
        }
        Template pmTemplate = null;
        //todo
//        if (sendSetting.getPmTemplateId() != null) {
//            pmTemplate = templateMapper.selectByPrimaryKey(sendSetting.getPmTemplateId());
//        }
        return getPmSendSettingVO(sendSetting, pmTemplate);
    }

    @Override
    public List<MsgServiceTreeVO> getMsgServiceTree() {
        List<SendSettingDTO> sendSettingDTOS = sendSettingMapper.selectAll();
        List<MsgServiceTreeVO> msgServiceTreeVOS = new ArrayList<>();
        MsgServiceTreeVO msgServiceTreeVO1 = new MsgServiceTreeVO();
        msgServiceTreeVO1.setParentId(0L);
        msgServiceTreeVO1.setId(1L);
        msgServiceTreeVO1.setName(LevelType.SITE.value());
        msgServiceTreeVO1.setCode(ResourceType.SITE.value());
        msgServiceTreeVOS.add(msgServiceTreeVO1);

        MsgServiceTreeVO msgServiceTreeVO2 = new MsgServiceTreeVO();
        msgServiceTreeVO2.setParentId(0L);
        msgServiceTreeVO2.setId(2L);
        msgServiceTreeVO2.setName(LevelType.ORGANIZATION.value());
        msgServiceTreeVO2.setCode(ResourceType.ORGANIZATION.value());
        msgServiceTreeVOS.add(msgServiceTreeVO2);

        MsgServiceTreeVO msgServiceTreeVO3 = new MsgServiceTreeVO();
        msgServiceTreeVO3.setParentId(0L);
        msgServiceTreeVO3.setId(3L);
        msgServiceTreeVO3.setName(LevelType.PROJECT.value());
        msgServiceTreeVO3.setCode(ResourceType.PROJECT.value());
        msgServiceTreeVOS.add(msgServiceTreeVO3);

        Map<String, Set<String>> categoryMap = new HashMap<>();
        categoryMap.put(ResourceType.SITE.value(), new HashSet<>());
        categoryMap.put(ResourceType.ORGANIZATION.value(), new HashSet<>());
        categoryMap.put(ResourceType.PROJECT.value(), new HashSet<>());
        for (SendSettingDTO sendSettingDTO : sendSettingDTOS) {
            Set<String> categoryCodes = categoryMap.get(sendSettingDTO.getLevel());
            if (categoryCodes != null) {
                categoryCodes.add(sendSettingDTO.getCategoryCode());
            }
        }
        getSecondMsgServiceTreeVOS(categoryMap, msgServiceTreeVOS, sendSettingDTOS);


        return msgServiceTreeVOS;
    }

    @Override
    public SendSettingDTO updateSendSetting(SendSettingDTO updateDTO) {
        SendSettingDTO sendSettingDTO = sendSettingMapper.selectByPrimaryKey(updateDTO);
        if (StringUtils.isEmpty(sendSettingDTO)) {
            throw new CommonException(SEND_SETTING_DOES_NOT_EXIST);
        }

        if (sendSettingMapper.updateByPrimaryKeySelective(updateDTO) != 1) {
            throw new CommonException(SEND_SETTING_UPDATE_EXCEPTION);
        }
        return updateDTO;
    }

    private void getSecondMsgServiceTreeVOS(Map<String, Set<String>> categoryMap, List<MsgServiceTreeVO> msgServiceTreeVOS, List<SendSettingDTO> sendSettingDTOS) {
        int i = 4;
        for (String level : categoryMap.keySet()) {
            for (String categoryCode : categoryMap.get(level)) {
                MsgServiceTreeVO msgServiceTreeVO = new MsgServiceTreeVO();
                if (level.equals(ResourceType.SITE.value())) {
                    msgServiceTreeVO.setParentId(1L);
                } else if (level.equals(ResourceType.ORGANIZATION.value())) {
                    msgServiceTreeVO.setParentId(2L);
                } else {
                    msgServiceTreeVO.setParentId(3L);
                }

                SendSettingCategoryDTO categoryDTO = new SendSettingCategoryDTO();
                categoryDTO.setCode(categoryCode);
                categoryDTO = sendSettingCategoryMapper.selectOne(categoryDTO);
                msgServiceTreeVO.setName(categoryDTO.getName());
                msgServiceTreeVO.setId((long) i);
                msgServiceTreeVO.setCode(categoryDTO.getCode());
                msgServiceTreeVOS.add(msgServiceTreeVO);
                int secondParentId = i;
                i = i + 1;

                i = getThirdMsgServiceTreeVOS(sendSettingDTOS, level, categoryCode, secondParentId, msgServiceTreeVOS, i);

            }
        }
    }

    private int getThirdMsgServiceTreeVOS(List<SendSettingDTO> sendSettingDTOS, String level, String categoryCode, Integer secondParentId, List<MsgServiceTreeVO> msgServiceTreeVOS, Integer i) {
        for (SendSettingDTO sendSettingDTO : sendSettingDTOS) {
            if (sendSettingDTO.getLevel().equals(level) && sendSettingDTO.getCategoryCode().equals(categoryCode)) {
                MsgServiceTreeVO treeVO = new MsgServiceTreeVO();
                treeVO.setParentId((long) secondParentId);
                treeVO.setId((long) i);
                treeVO.setName(sendSettingDTO.getName());
                treeVO.setEnabled(sendSettingDTO.getEnabled());
                treeVO.setCode(sendSettingDTO.getCode());
                msgServiceTreeVOS.add(treeVO);
                i = i + 1;
            }
        }
        return i;
    }

    /**
     * 根据 notify_send_setting{@link SendSettingDTO} 和 notify_template{@link Template}
     * 获取 EmailSendSettingVO {@link EmailSendSettingVO}
     *
     * @param sendSetting
     * @param template
     * @return
     */
    private EmailSendSettingVO getEmailSendSettingVO(SendSettingDTO sendSetting, Template template) {
        EmailSendSettingVO emailSendSettingVO = new EmailSendSettingVO();
        BeanUtils.copyProperties(sendSetting, emailSendSettingVO);
        emailSendSettingVO.setSendInstantly(sendSetting.getIsSendInstantly());
        emailSendSettingVO.setManualRetry(sendSetting.getIsManualRetry());
        if (template != null) {
            emailSendSettingVO
                    .setEmailTemplateId(template.getId())
                    .setEmailTemplateTitle(template.getTitle());
        }
        return emailSendSettingVO;
    }


    /**
     * 根据 notify_send_setting{@link SendSettingDTO} 和 notify_template{@link Template}
     * 获取 PmSendSettingVO {@link PmSendSettingVO}
     *
     * @param sendSetting
     * @param template
     * @return
     */
    private PmSendSettingVO getPmSendSettingVO(SendSettingDTO sendSetting, Template template) {
        PmSendSettingVO pmSendSettingVO = new PmSendSettingVO();
        BeanUtils.copyProperties(sendSetting, pmSendSettingVO);
        if (template != null) {
            pmSendSettingVO
                    .setPmTemplateId(template.getId())
                    .setPmTemplateTitle(template.getTitle());
        }
        return pmSendSettingVO;
    }

    @Override
    public WebHookVO.SendSetting getUnderProject() {
        WebHookVO.SendSetting sendSetting = new WebHookVO.SendSetting();
        //1.获取发送设置可选集合
        List<SendSettingDTO> sendSettingSelection = sendSettingMapper.select(new SendSettingDTO().setLevel(ResourceType.PROJECT.value()));
        if (CollectionUtils.isEmpty(sendSettingSelection)) {
            return sendSetting;
        }
        //2.获取发送设置类别集合
        Set<SendSettingCategoryDTO> sendSettingCategorySelection = sendSettingCategoryMapper.selectByCodeSet(sendSettingSelection.stream().map(SendSettingDTO::getCategoryCode).collect(Collectors.toSet()));
        //3.构造返回数据
        return sendSetting.setSendSettingSelection(new HashSet<>(sendSettingSelection)).setSendSettingCategorySelection(new HashSet<>(sendSettingCategorySelection));
    }

}
