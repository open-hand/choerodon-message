package io.choerodon.message.app.service.impl;

import io.choerodon.core.enums.ServiceNotifyType;
import io.choerodon.core.enums.TargetUserType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.message.api.vo.*;
import io.choerodon.message.app.service.MessageSettingC7nService;
import io.choerodon.message.app.service.MessageSettingTargetUserC7nService;
import io.choerodon.message.infra.dto.MessageSettingDTO;
import io.choerodon.message.infra.dto.TargetUserDTO;
import io.choerodon.message.infra.dto.iam.TenantDTO;
import io.choerodon.message.infra.enums.AgileNotifyTypeEnum;
import io.choerodon.message.infra.enums.DeleteResourceType;
import io.choerodon.message.infra.enums.DevopsNotifyTypeEnum;
import io.choerodon.message.infra.enums.SendingTypeEnum;
import io.choerodon.message.infra.feign.DevopsFeignClient;
import io.choerodon.message.infra.feign.IamFeignClient;
import io.choerodon.message.infra.mapper.MessageSettingC7nMapper;

import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.hzero.boot.platform.lov.feign.LovFeignClient;
import org.hzero.message.infra.constant.HmsgConstant;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * User: Mr.Wang
 * Date: 2019/12/3
 */
@Service
public class MessageSettingC7nServiceImpl implements MessageSettingC7nService {
    public static final String LOV_MESSAGE_CODE = "HMSG.TEMP_SERVER.SUBCATEGORY";
    private static final String RESOURCE_DELETE_CONFIRMATION = "RESOURCEDELETECONFIRMATION";
    private static final String ERROR_SAVE_MESSAGE_SETTING = "error.save.message.setting";
    private static final String ERROR_UPDATE_MESSAGE_SETTING = "error.createOrUpdateEmail.message.setting";
    private static final String ERROR_PARAM_INVALID = "error.param.invalid";
    private ModelMapper modelMapper = new ModelMapper();

    private MessageSettingC7nMapper messageSettingC7nMapper;

    private MessageSettingTargetUserC7nService messageSettingTargetUserService;

    private DevopsFeignClient devopsFeignClient;

    private IamFeignClient iamFeignClient;
    
    private LovFeignClient lovFeignClient;


    public MessageSettingC7nServiceImpl(MessageSettingC7nMapper messageSettingC7nMapper,
                                        MessageSettingTargetUserC7nService messageSettingTargetUserService,
                                        DevopsFeignClient devopsFeignClient,
                                        IamFeignClient iamFeignClient,
                                        LovFeignClient lovFeignClient) {
        this.messageSettingC7nMapper = messageSettingC7nMapper;
        this.messageSettingTargetUserService = messageSettingTargetUserService;
        this.devopsFeignClient = devopsFeignClient;
        this.iamFeignClient = iamFeignClient;
        this.lovFeignClient = lovFeignClient;
    }

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public MessageSettingWarpVO listMessageSettingByType(Long projectId, String notifyType, String eventName) {
        MessageSettingWarpVO messageSettingWarpVO = new MessageSettingWarpVO();
        //查询平台层的发送设置
        List<CustomMessageSettingVO> defaultMessageSettings = messageSettingC7nMapper.listDefaultSettingByNotifyType(notifyType);
        if (CollectionUtils.isEmpty(defaultMessageSettings)) {
            return messageSettingWarpVO;
        }
        //查询通知对象
        List<CustomMessageSettingVO> defaultMessageSettingList = messageSettingC7nMapper.listDefaultAndEnabledSettingByNotifyType(notifyType);
        assemblingSendSetting(defaultMessageSettingList, defaultMessageSettings);

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
        customMessageSettingList.stream().map(customMessageSettingVO -> {
            String lovCode = customMessageSettingVO.getSubcategoryCode();
            customMessageSettingVO.setGroupId(lovCode);
            return customMessageSettingVO;
        }).collect(Collectors.toList());

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

    private void assemblingSendSetting(List<CustomMessageSettingVO> targetUser, List<CustomMessageSettingVO> defaultMessageSettingList) {
        defaultMessageSettingList.forEach(customMessageSettingVO -> {
            if (SendingTypeEnum.EMAIL.getValue().equals(customMessageSettingVO.getSendingType()) && customMessageSettingVO.getEnabledFlag()) {
                customMessageSettingVO.setEmailEnabledFlag(Boolean.TRUE);
            }
            if (SendingTypeEnum.SMS.getValue().equals(customMessageSettingVO.getNotifyType()) && customMessageSettingVO.getEnabledFlag()) {
                customMessageSettingVO.setSmsEnabledFlag(Boolean.TRUE);
            }
            if (SendingTypeEnum.WEB.getValue().equals(customMessageSettingVO.getNotifyType()) && customMessageSettingVO.getEnabledFlag()) {
                customMessageSettingVO.setPmEnabledFlag(Boolean.TRUE);
            }
        });
        targetUser.forEach(customMessageSettingVO -> {
            defaultMessageSettingList.forEach(customMessageSettingVO1 -> {
                if (SendingTypeEnum.EMAIL.getValue().equals(customMessageSettingVO1.getSendingType())
                        && customMessageSettingVO1.getCode().equals(customMessageSettingVO.getCode())
                        && customMessageSettingVO1.getEnabledFlag()) {
                    customMessageSettingVO.setEmailEnabledFlag(Boolean.TRUE);
                }
                if (SendingTypeEnum.WEB.getValue().equals(customMessageSettingVO1.getSendingType())
                        && customMessageSettingVO1.getCode().equals(customMessageSettingVO.getCode())
                        && customMessageSettingVO1.getEnabledFlag()) {
                    customMessageSettingVO.setPmEnabledFlag(Boolean.TRUE);
                }
                if (SendingTypeEnum.SMS.getValue().equals(customMessageSettingVO1.getSendingType())
                        && customMessageSettingVO1.getCode().equals(customMessageSettingVO.getCode())
                        && customMessageSettingVO1.getEnabledFlag()) {
                    customMessageSettingVO.setSmsEnabledFlag(Boolean.TRUE);
                }
            });
        });
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateByType(Long projectId, String notifyType, List<CustomMessageSettingVO> messageSettingVOS) {
        if (messageSettingVOS.stream().anyMatch(settingVO -> !notifyType.equals(settingVO.getNotifyType()))) {
            throw new CommonException(ERROR_PARAM_INVALID);
        }
        List<CustomMessageSettingVO> defaultSettingList = messageSettingC7nMapper.listDefaultAndEnabledSettingByNotifyType(notifyType);
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
        if (messageSettingC7nMapper.insertSelective(messageSettingDTO) != 1) {
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
            messageSettingVO = messageSettingC7nMapper.getResourceDeleteSettingByOption(notifyType, projectId, code, envId, eventName);
            if (messageSettingVO == null) {
                messageSettingVO = messageSettingC7nMapper.getDefaultResourceDeleteSetting(notifyType, code, eventName);
            }
        } else {
            messageSettingVO = messageSettingC7nMapper.getSettingByTypeAndCode(notifyType, projectId, code);
            if (messageSettingVO == null) {
                messageSettingVO = messageSettingC7nMapper.getDefaultSettingByCode(notifyType, code);
            }
        }
        return messageSettingVO;
    }

    @Override
    public void deleteByTypeAndEnvId(String type, Long envId) {
        messageSettingC7nMapper.deleteByTypeAndEnvId(type, envId);
    }

    @Override
    public void updateMessageSetting(MessageSettingDTO messageSettingDTO) {
        if (messageSettingC7nMapper.updateByPrimaryKeySelective(messageSettingDTO) != 1) {
            throw new CommonException(ERROR_UPDATE_MESSAGE_SETTING);
        }
    }

    @Override
    public void disableNotifyTypeByCodeAndType(String code, String notiyfType) {
        if (ObjectUtils.isEmpty(code)) {
            throw new CommonException(ERROR_PARAM_INVALID);
        }
        MessageSettingDTO messageSettingDTO = new MessageSettingDTO();
        if (HmsgConstant.MessageType.WEB.equals(notiyfType)) {
            messageSettingDTO.setPmEnable(false);
        }
        if (HmsgConstant.MessageType.EMAIL.equals(notiyfType)) {
            messageSettingDTO.setEmailEnable(false);
        }
        if (HmsgConstant.MessageType.SMS.equals(notiyfType)) {
            messageSettingDTO.setSmsEnable(false);
        }
        messageSettingDTO.setCode(code);
        messageSettingDTO.setProjectId(TenantDTO.DEFAULT_TENANT_ID);
        messageSettingC7nMapper.updateOptional(messageSettingDTO);
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
        Set<String> groupIds = customMessageSettingList.stream().map(CustomMessageSettingVO::getGroupId).collect(Collectors.toSet());
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
            if (settingVO.getEnabledFlag()) {
                if (SendingTypeEnum.WEB.getValue().equals(settingVO.getSendSetting().getSendingType().trim())) {
                    settingVO.setPmEnabledFlag(true);
                }
                if (SendingTypeEnum.EMAIL.getValue().equals(settingVO.getSendSetting().getSendingType().trim())) {
                    settingVO.setEmailEnabledFlag(true);
                }
                if (SendingTypeEnum.SMS.getValue().equals(settingVO.getSendSetting().getSendingType().trim())) {
                    settingVO.setSmsEnabledFlag(true);
                }
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
                    targetUserVO.setUserId(TenantDTO.DEFAULT_TENANT_ID);
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
                List<UserVO> iamUserList = iamFeignClient.listUsersByIds(uids.toArray(new Long[20]), false).getBody();
                Map<Long, UserVO> iamUserMap = iamUserList.stream().collect(Collectors.toMap(UserVO::getId, v -> v));
                userList.forEach(user -> {
                    UserVO userDTO = iamUserMap.get(user.getUserId());
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
            List<CustomMessageSettingVO> customMessageSettingList = messageSettingC7nMapper.listMessageSettingByProjectIdAndEnvId(projectId, envId, notifyType);
            customMessageSettingList.stream().map(customMessageSettingVO -> {
                String lovCode = customMessageSettingVO.getSubcategoryCode();
                customMessageSettingVO.setGroupId(lovCode);
                return customMessageSettingVO;
            });
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
        resorceDeleteSettingList.forEach(settingVO -> settingVO.setGroupId(settingVO.getEnvId().toString()));
        return resorceDeleteSettingList;
    }

    private List<CustomMessageSettingVO> handleDevopsOrAgileSettings(List<CustomMessageSettingVO> defaultMessageSettingList, Long projectId, String notifyType) {
        List<CustomMessageSettingVO> customMessageSettingList = messageSettingC7nMapper.listMessageSettingByProjectId(projectId, notifyType);
        Map<String, CustomMessageSettingVO> custommessageSettingVOMap = customMessageSettingList.stream().collect(Collectors.toMap(CustomMessageSettingVO::getCode, v -> v));
        defaultMessageSettingList.forEach(defaultMessageSetting -> {
            CustomMessageSettingVO customMessageSettingVO = custommessageSettingVOMap.get(defaultMessageSetting.getCode());
            if (customMessageSettingVO == null) {
                defaultMessageSetting.setProjectId(projectId);
                customMessageSettingList.add(defaultMessageSetting);
            } else {
                // 为devops自定义配置添加默认通知对象
                if (ServiceNotifyType.DEVOPS_NOTIFY.getTypeName().equals(notifyType)) {
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
            List<NotifyEventGroupVO> notifyEventGroupVOS = new ArrayList<>();
            List<String> stringList = messageSettingC7nMapper.listCategoryCode(notifyType);
            if (CollectionUtils.isEmpty(stringList)) {
                return Collections.emptyList();
            }
            stringList.forEach(s -> {
                NotifyEventGroupVO notifyEventGroupVO = new NotifyEventGroupVO();
                notifyEventGroupVO.setCategoryCode(s);
                Map<String, String> stringStringMap = lovFeignClient.queryLovValue(LOV_MESSAGE_CODE, TenantDTO.DEFAULT_TENANT_ID).stream().collect(Collectors.toMap(LovValueDTO::getValue, LovValueDTO::getMeaning));
                notifyEventGroupVO.setName(stringStringMap.get(s));
                notifyEventGroupVOS.add(notifyEventGroupVO);
            });
            return notifyEventGroupVOS;
        }
        if (ServiceNotifyType.RESOURCE_DELETE_NOTIFY.getTypeName().equals(notifyType)) {
            return devopsFeignClient.listByActive(projectId, true).getBody();
        }
        return new ArrayList<>();
    }

}
