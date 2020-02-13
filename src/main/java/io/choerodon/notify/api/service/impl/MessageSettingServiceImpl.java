package io.choerodon.notify.api.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.notify.NotifyType;
import io.choerodon.core.notify.ServiceNotifyType;
import io.choerodon.core.notify.TargetUserType;
import io.choerodon.notify.api.dto.MessageSettingVO;
import io.choerodon.notify.api.dto.UserDTO;
import io.choerodon.notify.api.service.MessageSettingService;
import io.choerodon.notify.api.service.MessageSettingTargetUserService;
import io.choerodon.notify.api.vo.CustomMessageSettingVO;
import io.choerodon.notify.api.vo.MessageSettingWarpVO;
import io.choerodon.notify.api.vo.NotifyEventGroupVO;
import io.choerodon.notify.api.vo.TargetUserVO;
import io.choerodon.notify.infra.dto.MessageSettingDTO;
import io.choerodon.notify.infra.dto.TargetUserDTO;
import io.choerodon.notify.infra.enums.AgileNotifyTypeEnum;
import io.choerodon.notify.infra.enums.DeleteResourceType;
import io.choerodon.notify.infra.enums.DevopsNotifyTypeEnum;
import io.choerodon.notify.infra.enums.SendingTypeEnum;
import io.choerodon.notify.infra.feign.BaseFeignClient;
import io.choerodon.notify.infra.feign.DevopsFeginClient;
import io.choerodon.notify.infra.mapper.MessageSettingMapper;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * User: Mr.Wang
 * Date: 2019/12/3
 */
@Service
public class MessageSettingServiceImpl implements MessageSettingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSettingServiceImpl.class);
    private static final String RESOURCE_DELETE_CONFIRMATION = "resourceDeleteConfirmation";
    private static final String ERROR_SAVE_MESSAGE_SETTING = "error.save.message.setting";
    private static final String ERROR_UPDATE_MESSAGE_SETTING = "error.update.message.setting";
    private static final String ERROR_PARAM_INVALID = "error.param.invalid";
    private ModelMapper modelMapper = new ModelMapper();

    private MessageSettingMapper messageSettingMapper;

    private MessageSettingTargetUserService messageSettingTargetUserService;

    private DevopsFeginClient devopsFeginClient;

    private BaseFeignClient baseFeignClient;

    public MessageSettingServiceImpl(MessageSettingMapper messageSettingMapper, MessageSettingTargetUserService messageSettingTargetUserService, DevopsFeginClient devopsFeginClient, BaseFeignClient baseFeignClient) {
        this.messageSettingMapper = messageSettingMapper;
        this.messageSettingTargetUserService = messageSettingTargetUserService;
        this.devopsFeginClient = devopsFeginClient;
        this.baseFeignClient = baseFeignClient;
    }

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public MessageSettingWarpVO listMessageSettingByType(Long projectId, String notifyType, String eventName) {
        MessageSettingWarpVO messageSettingWarpVO = new MessageSettingWarpVO();
        // 平台层没有启用任何发送设置
        List<CustomMessageSettingVO> defaultMessageSettingList = messageSettingMapper.listDefaultAndEnabledSettingByNotifyType(notifyType);
        if (CollectionUtils.isEmpty(defaultMessageSettingList)) {
            return messageSettingWarpVO;
        }
        // 计算平台层是否启用发送短信，站内信，邮件
        calculateStieSendSetting(defaultMessageSettingList);
        List<NotifyEventGroupVO> notifyEventGroupList = listEventGroupList(projectId, notifyType);
        // 资源删除验证，项目下没有启用的环境
        if (ServiceNotifyType.RESOURCE_DELETE_NOTIFY.getTypeName().equals(notifyType)
                && CollectionUtils.isEmpty(notifyEventGroupList)) {
            return messageSettingWarpVO;
        }
        List<CustomMessageSettingVO> customMessageSettingList = new ArrayList<>();
        if (ServiceNotifyType.AGILE_NOTIFY.getTypeName().equals(notifyType)
                || ServiceNotifyType.DEVOPS_NOTIFY.getTypeName().equals(notifyType)) {
            customMessageSettingList = handleDevopsOrAgileSettings(defaultMessageSettingList, projectId, notifyType);
        }
        if (ServiceNotifyType.RESOURCE_DELETE_NOTIFY.getTypeName().equals(notifyType)) {
            customMessageSettingList = handleResorceDeleteSettings(notifyEventGroupList, defaultMessageSettingList, projectId, notifyType);
        }

        // 计算通知对象
        calculateSendRole(customMessageSettingList);
        // 添加用户信息
        addUserInfo(customMessageSettingList);
        // 计算通知事件的名称
        calculateEventName(customMessageSettingList);
        if (!ObjectUtils.isEmpty(eventName)) {
            // 根据事件名称过滤事件
            customMessageSettingList = filterSettingListByEventName(customMessageSettingList, eventName);
            // 过滤事件分组
            notifyEventGroupList = filterEventGroupBySettingList(customMessageSettingList, notifyEventGroupList);
        }
        // 装配VO
        messageSettingWarpVO.setCustomMessageSettingList(sortEvent(notifyType, customMessageSettingList));
        messageSettingWarpVO.setNotifyEventGroupList(notifyEventGroupList);

        return messageSettingWarpVO;
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateByType(Long projectId, String notifyType, List<CustomMessageSettingVO> messageSettingVOS) {
        if (messageSettingVOS.stream().anyMatch(settingVO -> !notifyType.equals(settingVO.getNotifyType()))) {
            throw new CommonException(ERROR_PARAM_INVALID);
        }
        List<CustomMessageSettingVO> defaultSettingList = messageSettingMapper.listDefaultAndEnabledSettingByNotifyType(notifyType);
        Set<Long> defaultSettingIds = defaultSettingList.stream().map(CustomMessageSettingVO::getId).collect(Collectors.toSet());
        // 将非指定用户添加到userList
        calculateNotifyUsersByType(messageSettingVOS);
        List<CustomMessageSettingVO> defaultMessagesSettings = messageSettingVOS.stream().filter(settingVO -> defaultSettingIds.contains(settingVO.getId())).collect(Collectors.toList());
        List<CustomMessageSettingVO> customMessagesSettings = messageSettingVOS.stream().filter(settingVO -> !defaultSettingIds.contains(settingVO.getId())).collect(Collectors.toList());
        // 默认配置的修改
        defaultMessagesSettings.forEach(settingVO -> {
            MessageSettingDTO settingDTO = modelMapper.map(settingVO, MessageSettingDTO.class);
            settingDTO.setId(null);
            saveMessageSetting(settingDTO);
            if (!ServiceNotifyType.DEVOPS_NOTIFY.getTypeName().equals(settingVO.getNotifyType())) {
                List<TargetUserVO> userList = settingVO.getUserList();
                if (!CollectionUtils.isEmpty(userList)) {
                    settingVO.getUserList().forEach(user -> {
                        user.setMessageSettingId(settingDTO.getId());
                        messageSettingTargetUserService.save(modelMapper.map(user, TargetUserDTO.class));
                    });
                }
            }

        });
        // 自定配置修改
        customMessagesSettings.forEach(settingVO -> {
            MessageSettingDTO settingDTO = modelMapper.map(settingVO, MessageSettingDTO.class);
            updateMessageSetting(settingDTO);
            // devops消息不更新通知对象
            if (!ServiceNotifyType.DEVOPS_NOTIFY.getTypeName().equals(settingVO.getNotifyType())) {
                // 删除旧数据
                if (!CollectionUtils.isEmpty(messageSettingTargetUserService.getBySettingId(settingVO.getId()))) {
                    messageSettingTargetUserService.deleteBySettingId(settingVO.getId());
                }
                // 添加新数据
                List<TargetUserVO> userList = settingVO.getUserList();
                if (!CollectionUtils.isEmpty(userList)) {
                    userList.forEach(user -> {
                        user.setMessageSettingId(settingDTO.getId());
                        messageSettingTargetUserService.save(modelMapper.map(user, TargetUserDTO.class));
                    });

                }
            }
        });
    }

    @Override
    public void saveMessageSetting(MessageSettingDTO messageSettingDTO) {
        if (messageSettingMapper.insertSelective(messageSettingDTO) != 1) {
            throw new CommonException(ERROR_SAVE_MESSAGE_SETTING);
        }
    }

    @Override
    public MessageSettingVO getSettingByCode(Long projectId, String notifyType, String code, Long envId, String eventName) {
        MessageSettingVO messageSettingVO;

        if (ServiceNotifyType.RESOURCE_DELETE_NOTIFY.getTypeName().equals(notifyType)) {
            if (envId == null || ObjectUtils.isEmpty(eventName)) {
                throw new CommonException(ERROR_PARAM_INVALID);
            }
            messageSettingVO = messageSettingMapper.getResourceDeleteSettingByOption(notifyType, projectId, code, envId, eventName);
            if (messageSettingVO == null) {
                messageSettingVO = messageSettingMapper.getDefaultResourceDeleteSetting(notifyType, code, eventName);
            }
        } else {
            messageSettingVO = messageSettingMapper.getSettingByTypeAndCode(notifyType, projectId, code);
            if (messageSettingVO == null) {
                messageSettingVO = messageSettingMapper.getDefaultSettingByCode(notifyType, code);
            }
        }
        return messageSettingVO;
    }

    @Override
    public void deleteByTypeAndEnvId(String type, Long envId) {
        messageSettingMapper.deleteByTypeAndEnvId(type, envId);
    }

    @Override
    public void updateMessageSetting(MessageSettingDTO messageSettingDTO) {
        if (messageSettingMapper.updateByPrimaryKeySelective(messageSettingDTO) != 1) {
            throw new CommonException(ERROR_UPDATE_MESSAGE_SETTING);
        }
    }

    @Override
    public void disableNotifyTypeByCodeAndType(String code, String notiyfType) {
        if (ObjectUtils.isEmpty(code)) {
            throw new CommonException(ERROR_PARAM_INVALID);
        }
        MessageSettingDTO messageSettingDTO = new MessageSettingDTO();
        if (NotifyType.PM.getValue().equals(notiyfType)) {
            messageSettingDTO.setPmEnable(false);
        }
        if (NotifyType.EMAIL.getValue().equals(notiyfType)) {
            messageSettingDTO.setEmailEnable(false);
        }
        if (NotifyType.SMS.getValue().equals(notiyfType)) {
            messageSettingDTO.setSmsEnable(false);
        }
        Example example = new Example(MessageSettingDTO.class);
        example.createCriteria().andEqualTo("code", code).andNotEqualTo("projectId", 0);
        messageSettingMapper.updateByExampleSelective(messageSettingDTO, example);
    }

    private List<CustomMessageSettingVO> sortEvent(String notifyType, List<CustomMessageSettingVO> customMessageSettingList) {
        // 设置排序大小
        if (ServiceNotifyType.AGILE_NOTIFY.getTypeName().equals(notifyType)) {
            customMessageSettingList.forEach(settingVO -> {
                Integer order = AgileNotifyTypeEnum.orderMapping.get(settingVO.getCode());
                settingVO.setOrder(order == null ? 0 : order);
            });
        }
        if (ServiceNotifyType.DEVOPS_NOTIFY.getTypeName().equals(notifyType)) {
            customMessageSettingList.forEach(settingVO -> {
                Integer order = DevopsNotifyTypeEnum.orderMapping.get(settingVO.getCode());
                settingVO.setOrder(order == null ? 0 : order);
            });
        }
        if (ServiceNotifyType.RESOURCE_DELETE_NOTIFY.getTypeName().equals(notifyType)) {
            customMessageSettingList.forEach(settingVO -> {
                Integer order = DeleteResourceType.orderMapping.get(settingVO.getEventName());
                settingVO.setOrder(order == null ? 0 : order);
            });
        }
        // 排序
        return customMessageSettingList.stream().sorted(Comparator.comparing(CustomMessageSettingVO::getOrder)).collect(Collectors.toList());

    }

    private List<NotifyEventGroupVO> filterEventGroupBySettingList(List<CustomMessageSettingVO> customMessageSettingList, List<NotifyEventGroupVO> notifyEventGroupList) {
        Set<Long> groupIds = customMessageSettingList.stream().map(CustomMessageSettingVO::getGroupId).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(groupIds)) {
            return new ArrayList<>();
        }
        return notifyEventGroupList.stream().filter(group -> groupIds.contains(group.getId())).collect(Collectors.toList());
    }

    private List<CustomMessageSettingVO> filterSettingListByEventName(List<CustomMessageSettingVO> customMessageSettingList, String eventName) {
        return customMessageSettingList.stream().filter(settingVO -> settingVO.getName().contains(eventName)).collect(Collectors.toList());
    }

    private void calculateStieSendSetting(List<CustomMessageSettingVO> defaultMessageSettingList) {
        defaultMessageSettingList.forEach(settingVO -> {
            if (Boolean.TRUE.equals(settingVO.getSendSetting().getPmEnabledFlag())
                    && !CollectionUtils.isEmpty(settingVO.getSendSetting().getTemplates())
                    && settingVO.getSendSetting().getTemplates().stream().anyMatch(template -> SendingTypeEnum.PM.getValue().equals(template.getSendingType()))) {
                settingVO.setPmEnabledFlag(true);
            }
            if (Boolean.TRUE.equals(settingVO.getSendSetting().getEmailEnabledFlag())
                    && !CollectionUtils.isEmpty(settingVO.getSendSetting().getTemplates())
                    && settingVO.getSendSetting().getTemplates().stream().anyMatch(template -> SendingTypeEnum.EMAIL.getValue().equals(template.getSendingType()))) {
                settingVO.setEmailEnabledFlag(true);
            }
            if (Boolean.TRUE.equals(settingVO.getSendSetting().getSmsEnabledFlag())
                    && !CollectionUtils.isEmpty(settingVO.getSendSetting().getTemplates())
                    && settingVO.getSendSetting().getTemplates().stream().anyMatch(template -> SendingTypeEnum.SMS.getValue().equals(template.getSendingType()))) {
                settingVO.setSmsEnabledFlag(true);
            }
        });

    }

    private void calculateNotifyUsersByType(List<CustomMessageSettingVO> messageSettingVOS) {

        // 添加指定用户
        messageSettingVOS.forEach(settingVO -> {
            List<TargetUserVO> userList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(settingVO.getSpecifierIds())) {
                settingVO.getSpecifierIds().forEach(uid -> {
                    TargetUserVO targetUserVO = new TargetUserVO();
                    targetUserVO.setType(TargetUserType.SPECIFIER.getTypeName());
                    targetUserVO.setUserId(uid);
                    userList.add(targetUserVO);
                });
            }

            // 添加非指定用户
            Set<String> sendRoleList = settingVO.getSendRoleList();
            if (!CollectionUtils.isEmpty(sendRoleList)) {
                sendRoleList.stream().filter(role -> !TargetUserType.SPECIFIER.getTypeName().equals(role)).forEach(role -> {
                    TargetUserVO targetUserVO = new TargetUserVO();
                    targetUserVO.setType(role);
                    targetUserVO.setUserId(0L);
                    userList.add(targetUserVO);
                });
            }
            settingVO.setUserList(userList);
        });
    }

    private void calculateEventName(List<CustomMessageSettingVO> customMessageSettingList) {
        customMessageSettingList.stream()
                .filter(settingVO -> RESOURCE_DELETE_CONFIRMATION.equals(settingVO.getCode()))
                .forEach(settingVO -> settingVO.setName(DeleteResourceType.nameMapping.get(settingVO.getEventName())));
    }

    private void addUserInfo(List<CustomMessageSettingVO> customMessageSettingList) {
        customMessageSettingList.forEach(settingVO -> {
            List<TargetUserVO> userList = settingVO.getUserList();
            if (!CollectionUtils.isEmpty(userList)) {
                List<Long> uids = userList.stream().map(TargetUserVO::getUserId).collect(Collectors.toList());
                List<UserDTO> iamUserList = baseFeignClient.listUsersByIds(uids.toArray(new Long[20]), false).getBody();
                Map<Long, UserDTO> iamUserMap = iamUserList.stream().collect(Collectors.toMap(UserDTO::getId, v -> v));
                userList.forEach(user -> {
                    UserDTO userDTO = iamUserMap.get(user.getUserId());
                    user.setRealName(userDTO.getRealName());
                    user.setLoginName(userDTO.getLoginName());
                });
            }
        });
    }

    private void calculateSendRole(List<CustomMessageSettingVO> customMessageSettingList) {
        customMessageSettingList.forEach(settingVO -> {
            List<TargetUserVO> userList = settingVO.getUserList();
            if (!CollectionUtils.isEmpty(userList)) {
                // 处理id和userId
                userList.forEach(user -> user.setId(user.getUserId()));
                Set<String> roleList = userList.stream()
                        .map(TargetUserVO::getType)
                        .collect(Collectors.toSet());
                // 设置通知角色
                settingVO.setSendRoleList(roleList);
                if (ServiceNotifyType.DEVOPS_NOTIFY.getTypeName().equals(settingVO.getNotifyType())) {
                    settingVO.setNotifyObject(TargetUserType.nameMapping.get(settingVO.getUserList().get(0).getType()));
                }
                // 设置要通知的非指定用户
                settingVO.setUserList(userList.stream().filter(user -> TargetUserType.SPECIFIER.getTypeName().equals(user.getType())).collect(Collectors.toList()));
                settingVO.setSpecifierIds(settingVO.getUserList().stream().map(TargetUserVO::getUserId).collect(Collectors.toSet()));

            }
        });
    }

    private List<CustomMessageSettingVO> handleResorceDeleteSettings(List<NotifyEventGroupVO> notifyEventGroupList, List<CustomMessageSettingVO> defaultMessageSettingList, Long projectId, String notifyType) {
        List<CustomMessageSettingVO> resorceDeleteSettingList = new ArrayList<>();
        notifyEventGroupList.stream().map(NotifyEventGroupVO::getId).forEach(envId -> {
            List<CustomMessageSettingVO> customMessageSettingList = messageSettingMapper.listMessageSettingByProjectIdAndEnvId(projectId, envId, notifyType);
            Map<String, CustomMessageSettingVO> custommessageSettingVOMap = customMessageSettingList.stream().collect(Collectors.toMap(CustomMessageSettingVO::getEventName, v -> v));
            defaultMessageSettingList.forEach(setting -> {
                CustomMessageSettingVO customMessageSettingVO = custommessageSettingVOMap.get(setting.getEventName());
                if (customMessageSettingVO == null) {
                    CustomMessageSettingVO messageSettingVO = new CustomMessageSettingVO();
                    modelMapper.map(setting, messageSettingVO);
                    messageSettingVO.setProjectId(projectId);
                    messageSettingVO.setEnvId(envId);
                    customMessageSettingList.add(messageSettingVO);
                } else {
                    customMessageSettingVO.setSmsEnabledFlag(setting.getSmsEnabledFlag());
                    customMessageSettingVO.setEmailEnabledFlag(setting.getEmailEnabledFlag());
                    customMessageSettingVO.setPmEnabledFlag(setting.getPmEnabledFlag());
                }
            });
            resorceDeleteSettingList.addAll(customMessageSettingList);
        });
        resorceDeleteSettingList.forEach(settingVO -> settingVO.setGroupId(settingVO.getEnvId()));
        return resorceDeleteSettingList;
    }

    private List<CustomMessageSettingVO> handleDevopsOrAgileSettings(List<CustomMessageSettingVO> defaultMessageSettingList, Long projectId, String notifyType) {
        List<CustomMessageSettingVO> customMessageSettingList = messageSettingMapper.listMessageSettingByProjectId(projectId, notifyType);
        Map<String, CustomMessageSettingVO> custommessageSettingVOMap = customMessageSettingList.stream().collect(Collectors.toMap(CustomMessageSettingVO::getCode, v -> v));
        defaultMessageSettingList.forEach(defaultMessageSetting -> {
            CustomMessageSettingVO customMessageSettingVO = custommessageSettingVOMap.get(defaultMessageSetting.getCode());
            if (customMessageSettingVO == null) {
                defaultMessageSetting.setProjectId(projectId);
                customMessageSettingList.add(defaultMessageSetting);
            } else {
                // 为devops自定义配置添加默认通知对象
                if(ServiceNotifyType.DEVOPS_NOTIFY.getTypeName().equals(notifyType)) {
                    customMessageSettingVO.setUserList(defaultMessageSetting.getUserList());
                }
                customMessageSettingVO.setSmsEnabledFlag(defaultMessageSetting.getSmsEnabledFlag());
                customMessageSettingVO.setEmailEnabledFlag(defaultMessageSetting.getEmailEnabledFlag());
                customMessageSettingVO.setPmEnabledFlag(defaultMessageSetting.getPmEnabledFlag());
            }
        });
        return customMessageSettingList;
    }

    private List<NotifyEventGroupVO> listEventGroupList(Long projectId, String notifyType) {
        if (ServiceNotifyType.AGILE_NOTIFY.getTypeName().equals(notifyType)
                || ServiceNotifyType.DEVOPS_NOTIFY.getTypeName().equals(notifyType)) {
            return messageSettingMapper.listCategoriesBySettingType(notifyType);
        }
        if (ServiceNotifyType.RESOURCE_DELETE_NOTIFY.getTypeName().equals(notifyType)) {
            return devopsFeginClient.listByActive(projectId, true).getBody();
        }
        return new ArrayList<>();
    }

}
