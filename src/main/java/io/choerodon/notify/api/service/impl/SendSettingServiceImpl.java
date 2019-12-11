package io.choerodon.notify.api.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.notify.Level;
import io.choerodon.core.notify.ServiceNotifyType;
import io.choerodon.notify.api.dto.*;
import io.choerodon.notify.api.service.SendSettingService;
import io.choerodon.notify.api.validator.CommonValidator;
import io.choerodon.notify.api.vo.WebHookVO;
import io.choerodon.notify.infra.dto.*;
import io.choerodon.notify.infra.enums.LevelType;
import io.choerodon.notify.infra.mapper.*;
import io.choerodon.swagger.notify.NotifyBusinessTypeScanData;
import io.choerodon.web.util.PageableHelper;

@Service
public class SendSettingServiceImpl implements SendSettingService {

    public static final String SEND_SETTING_DOES_NOT_EXIST = "error.send.setting.not.exist";
    public static final String SEND_SETTING_UPDATE_EXCEPTION = "error.send.setting.update";
    private SendSettingMapper sendSettingMapper;
    private SendSettingCategoryMapper sendSettingCategoryMapper;
    private TemplateMapper templateMapper;
    private MessageSettingMapper messageSettingMapper;
    private MessageSettingTargetUserMapper messageSettingTargetUserMapper;
    private final ModelMapper modelMapper = new ModelMapper();

    public SendSettingServiceImpl(SendSettingMapper sendSettingMapper, SendSettingCategoryMapper sendSettingCategoryMapper, TemplateMapper templateMapper,
                                  MessageSettingMapper messageSettingMapper, MessageSettingTargetUserMapper messageSettingTargetUserMapper) {
        this.sendSettingMapper = sendSettingMapper;
        this.sendSettingCategoryMapper = sendSettingCategoryMapper;
        this.templateMapper = templateMapper;
        this.messageSettingMapper = messageSettingMapper;
        this.messageSettingTargetUserMapper = messageSettingTargetUserMapper;
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
        businessTypes.forEach(t -> {
            SendSettingDTO sendSettingDTO = new SendSettingDTO();
            BeanUtils.copyProperties(t, sendSettingDTO);
            SendSettingDTO query = sendSettingMapper.selectOne(new SendSettingDTO(sendSettingDTO.getCode()));
            if (query == null) {
                sendSettingMapper.insertSelective(sendSettingDTO);
            } else {
                query.setName(sendSettingDTO.getName());
                query.setDescription(sendSettingDTO.getDescription());
                query.setLevel(sendSettingDTO.getLevel());
                query.setCategoryCode(sendSettingDTO.getCategoryCode());
                query.setPmEnabledFlag(sendSettingDTO.getPmEnabledFlag());
                query.setEmailEnabledFlag(sendSettingDTO.getEmailEnabledFlag());
                query.setSmsEnabledFlag(sendSettingDTO.getSmsEnabledFlag());
                query.setWebhookEnabledFlag(sendSettingDTO.getWebhookEnabledFlag());
                sendSettingMapper.updateByPrimaryKeySelective(query);
            }

            if (t.getLevel().equals(Level.PROJECT.getValue()) && !t.getNotifyType().equals(ServiceNotifyType.DEFAULT_NOTIFY.getTypeName())) {
                updateMsgSetting(t);
            }
        });
    }

    @Override
    public List<SendSettingDetailTreeDTO> queryByLevelAndAllowConfig(String level, boolean allowConfig) {
        if (level != null) {
            CommonValidator.validatorLevel(level);
        }
        List<SendSettingDetailDTO> list = sendSettingMapper.queryByLevelAndAllowConfig(level, allowConfig);
        List<SendSettingDetailTreeDTO> sendSettingDetailTreeDTOS = new ArrayList<>();

        Map<String, Set<String>> categoryMap = new HashMap<>();
        categoryMap.put(ResourceType.valueOf(level.toUpperCase()).value(), new HashSet<>());
        for (SendSettingDetailDTO sendSettingDTO : list) {
            Set<String> categoryCodes = categoryMap.get(sendSettingDTO.getLevel());
            if (categoryCodes != null) {
                categoryCodes.add(sendSettingDTO.getCategoryCode());
            }
        }
        getSecondSendSettingDetailTreeDTOS(categoryMap, sendSettingDetailTreeDTOS, list);


        return sendSettingDetailTreeDTOS.stream().filter(s -> (s.getEmailTemplateId() != null || s.getPmTemplateId() != null || s.getSmsTemplateId() != null) || s.getParentId() == 0)
                .collect(Collectors.toList());
    }

    private void getSecondSendSettingDetailTreeDTOS(Map<String, Set<String>> categoryMap, List<SendSettingDetailTreeDTO> sendSettingDetailTreeDTOS, List<SendSettingDetailDTO> sendSettingDetailDTOS) {
        int i = 1;
        for (String level : categoryMap.keySet()) {
            for (String categoryCode : categoryMap.get(level)) {
                SendSettingDetailTreeDTO sendSettingDetailTreeDTO = new SendSettingDetailTreeDTO();
                sendSettingDetailTreeDTO.setParentId(0L);

                SendSettingCategoryDTO categoryDTO = new SendSettingCategoryDTO();
                categoryDTO.setCode(categoryCode);
                categoryDTO = sendSettingCategoryMapper.selectOne(categoryDTO);
                sendSettingDetailTreeDTO.setName(categoryDTO.getName());
                sendSettingDetailTreeDTO.setSequenceId((long) i);
                sendSettingDetailTreeDTO.setCode(categoryDTO.getCode());
                //防止被过滤掉
                sendSettingDetailTreeDTO.setEmailTemplateId(0L);
                sendSettingDetailTreeDTOS.add(sendSettingDetailTreeDTO);
                int secondParentId = i;
                i = i + 1;

                i = getThirdSendSettingDetailTreeDTOS(sendSettingDetailDTOS, level, categoryCode, secondParentId, sendSettingDetailTreeDTOS, i);

            }
        }
    }

    private int getThirdSendSettingDetailTreeDTOS(List<SendSettingDetailDTO> sendSettingDetailDTOS, String level, String categoryCode, Integer secondParentId, List<SendSettingDetailTreeDTO> sendSettingDetailTreeDTOS, Integer i) {
        for (SendSettingDetailDTO sendSettingDetailDTO : sendSettingDetailDTOS) {
            if (sendSettingDetailDTO.getLevel().equals(level) && sendSettingDetailDTO.getCategoryCode().equals(categoryCode)) {
                SendSettingDetailTreeDTO sendSettingDetailTreeDTO = new SendSettingDetailTreeDTO();
                BeanUtils.copyProperties(sendSettingDetailDTO, sendSettingDetailTreeDTO);
                sendSettingDetailTreeDTO.setParentId((long) secondParentId);
                sendSettingDetailTreeDTO.setSequenceId((long) i);
                sendSettingDetailTreeDTOS.add(sendSettingDetailTreeDTO);
                i = i + 1;
            }
        }
        return i;
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

        updateDTO.setObjectVersionNumber(sendSettingDTO.getObjectVersionNumber());
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

    @Override
    public WebHookVO.SendSetting getUnderProject() {
        WebHookVO.SendSetting sendSetting = new WebHookVO.SendSetting();
        //1.获取WebHook 发送设置可选集合(启用,且启用WebHook的发送设置)
        List<SendSettingDTO> sendSettingSelection = sendSettingMapper.select(new SendSettingDTO().setEnabled(true).setLevel(ResourceType.PROJECT.value()).setWebhookEnabledFlag(true));
        if (CollectionUtils.isEmpty(sendSettingSelection)) {
            return sendSetting;
        }
        //2.获取发送设置类别集合
        Set<SendSettingCategoryDTO> sendSettingCategorySelection = sendSettingCategoryMapper.selectByCodeSet(sendSettingSelection.stream().map(SendSettingDTO::getCategoryCode).collect(Collectors.toSet()));
        //3.构造返回数据
        return sendSetting.setSendSettingSelection(new HashSet<>(sendSettingSelection)).setSendSettingCategorySelection(new HashSet<>(sendSettingCategorySelection));
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

    @Override
    public SendSettingDTO queryByCode(String code) {
        SendSettingDTO sendSettingDTO = new SendSettingDTO();
        sendSettingDTO.setCode(code);
        return sendSettingMapper.selectOne(sendSettingDTO);
    }

    /**
     * 更新MessageSetting
     *
     * @param typeScanData
     */
    private void updateMsgSetting(NotifyBusinessTypeScanData typeScanData) {
        MessageSettingDTO queryDTO = messageSettingMapper.queryByCodeWithoutProjectId(typeScanData.getCode());
        if (queryDTO == null) {
            MessageSettingDTO messageSettingDTO = new MessageSettingDTO();
            BeanUtils.copyProperties(typeScanData, messageSettingDTO);
            messageSettingDTO.setEmailEnable(typeScanData.getProEmailEnabledFlag());
            messageSettingDTO.setPmEnable(typeScanData.getProPmEnabledFlag());
            messageSettingMapper.insertSelective(messageSettingDTO);
            if (typeScanData.getTargetUserType().length > 0) {
                for (String targetUserType : typeScanData.getTargetUserType()) {
                    TargetUserDTO targetUserDTO = new TargetUserDTO();
                    targetUserDTO.setMessageSettingId(messageSettingDTO.getId());
                    targetUserDTO.setType(targetUserType);
                    messageSettingTargetUserMapper.insertSelective(targetUserDTO);
                }
            }
        } else {
            queryDTO.setPmEnable(typeScanData.getProPmEnabledFlag());
            queryDTO.setEmailEnable(typeScanData.getProEmailEnabledFlag());
            queryDTO.setNotifyType(typeScanData.getNotifyType());
            if (messageSettingMapper.updateByPrimaryKeySelective(queryDTO) != 1) {
                throw new CommonException("error.insert.message.setting");
            }
            updateTargetUser(typeScanData, queryDTO.getId());
        }
    }

    /**
     * 更新targetUser
     *
     * @param typeScanData
     * @param mgsSettingId
     */
    private void updateTargetUser(NotifyBusinessTypeScanData typeScanData, Long mgsSettingId) {
        List<String> oldTypeList = messageSettingTargetUserMapper.listByMsgSettingId(mgsSettingId).stream().map(TargetUserDTO::getType).collect(Collectors.toList());
        List<String> newTypeList = Arrays.asList(typeScanData.getTargetUserType());
        List<String> typeList = new ArrayList<>(newTypeList);
        if (oldTypeList != null) {
            newTypeList.forEach(type -> {
                if (oldTypeList.contains(type)) {
                    oldTypeList.remove(type);
                    typeList.remove(type);
                }
            });
        }

        if (oldTypeList != null) {
            oldTypeList.forEach(oldType -> {
                TargetUserDTO targetUserDTO = new TargetUserDTO();
                targetUserDTO.setMessageSettingId(mgsSettingId);
                targetUserDTO.setType(oldType);
                messageSettingTargetUserMapper.delete(targetUserDTO);
            });
        }

        typeList.forEach(newType -> {
            TargetUserDTO targetUserDTO = new TargetUserDTO();
            targetUserDTO.setMessageSettingId(mgsSettingId);
            targetUserDTO.setType(newType);
            messageSettingTargetUserMapper.insertSelective(targetUserDTO);
        });
    }
}
