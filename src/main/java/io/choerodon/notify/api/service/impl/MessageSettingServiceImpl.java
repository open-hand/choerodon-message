package io.choerodon.notify.api.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.notify.ServiceNotifyType;
import io.choerodon.core.notify.TargetUserType;
import io.choerodon.notify.api.dto.MessageSettingCategoryDTO;
import io.choerodon.notify.api.dto.MessageSettingVO;
import io.choerodon.notify.api.dto.UserDTO;
import io.choerodon.notify.api.service.MessageSettingService;
import io.choerodon.notify.api.service.SendSettingService;
import io.choerodon.notify.api.vo.CustomMessageSettingVO;
import io.choerodon.notify.api.vo.MessageSettingWarpVO;
import io.choerodon.notify.api.vo.NotifyEventGroupVO;
import io.choerodon.notify.api.vo.TargetUserVO;
import io.choerodon.notify.infra.dto.MessageSettingDTO;
import io.choerodon.notify.infra.dto.TargetUserDTO;
import io.choerodon.notify.infra.feign.BaseFeignClient;
import io.choerodon.notify.infra.feign.DevopsFeginClient;
import io.choerodon.notify.infra.feign.UserFeignClient;
import io.choerodon.notify.infra.mapper.MessageSettingMapper;
import io.choerodon.notify.infra.mapper.MessageSettingTargetUserMapper;
import io.choerodon.notify.infra.mapper.SendSettingMapper;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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
    @Autowired
    private MessageSettingMapper messageSettingMapper;

    @Autowired
    private MessageSettingTargetUserMapper messageSettingTargetUserMapper;

    @Autowired
    private SendSettingMapper sendSettingMapper;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private DevopsFeginClient devopsFeginClient;

    @Autowired
    private SendSettingService sendSettingService;

    @Autowired
    private BaseFeignClient baseFeignClient;

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public List<MessageSettingCategoryDTO> listMessageSetting(Long projectId, MessageSettingVO messageSettingVO) {
        //如果根据project_id查不到数据，那么查默认的数据
        MessageSettingDTO messageSettingDTO = new MessageSettingDTO();
        messageSettingDTO.setProjectId(projectId);
        List<MessageSettingDTO> settingDTOS = messageSettingMapper.select(messageSettingDTO);
        if (Objects.isNull(settingDTOS) || settingDTOS.size() == 0) {
            return messageSettingMapper.listMessageSettingByCondition(null, modelMapper.map(messageSettingVO, MessageSettingDTO.class));
        } else {
            //如果project_id在后端有数据，那么有的照旧，没有的用默认的数据补上
            return messageSettingMapper.listMessageSettingByCondition(projectId, modelMapper.map(messageSettingVO, MessageSettingDTO.class));
        }
    }

    @Override
    @Transactional
    public void updateMessageSetting(Long projectId, List<MessageSettingVO> messageSettingVOS) {
        if (messageSettingVOS == null || messageSettingVOS.size() == 0 || projectId == null) {
            return;
        }
        MessageSettingDTO messageSettingDTO = new MessageSettingDTO();
        messageSettingDTO.setNotifyType(messageSettingVOS.get(0).getNotifyType());
        //判断这个是否首次修改
        TargetUserDTO targetUserDTO = new TargetUserDTO();
        messageSettingVOS.stream().forEach(e -> {
            messageSettingDTO.setProjectId(projectId);
            messageSettingDTO.setCode(e.getCode());
            List<MessageSettingDTO> settingDTOS = messageSettingMapper.select(messageSettingDTO);
            //如果是首次修改则插入带ProjectId的数据
            if (Objects.isNull(settingDTOS) || settingDTOS.size() == 0) {
                e.setProjectId(projectId);
                e.setId(null);
                messageSettingMapper.insert(modelMapper.map(e, MessageSettingDTO.class));
                MessageSettingDTO condition = new MessageSettingDTO();
                condition.setProjectId(projectId);
                condition.setCode(e.getCode());
                MessageSettingDTO messageSettingDTO1 = messageSettingMapper.selectOne(condition);
                List<TargetUserDTO> targetUserDTOS = e.getTargetUserDTOS();
                targetUserDTOS.stream().forEach(v -> {
                    v.setId(null);
                    v.setMessageSettingId(messageSettingDTO1.getId());
                    messageSettingTargetUserMapper.insert(v);
                });
            } else {
                //否则修改原来的数据
                //先删除targetUser
                TargetUserDTO targetUserDTO1 = new TargetUserDTO();
                targetUserDTO1.setMessageSettingId(e.getId());
                messageSettingTargetUserMapper.delete(targetUserDTO1);
                //再删除MessageSetting
                MessageSettingDTO messageSettingDTO1 = new MessageSettingDTO();
                messageSettingDTO1.setId(e.getId());
                messageSettingMapper.delete(messageSettingDTO1);

                //然后插入更新后的数据
                e.setProjectId(projectId);
                e.setId(null);
                messageSettingMapper.insert(modelMapper.map(e, MessageSettingDTO.class));
                List<TargetUserDTO> targetUserDTOS = e.getTargetUserDTOS();
                MessageSettingDTO condition = new MessageSettingDTO();
                condition.setProjectId(projectId);
                condition.setCode(e.getCode());
                MessageSettingDTO messageSettingDTO2 = messageSettingMapper.selectOne(condition);
                targetUserDTOS.stream().forEach(v -> {
                    v.setId(null);
                    v.setMessageSettingId(messageSettingDTO2.getId());
                    messageSettingTargetUserMapper.insert(v);
                });
            }
        });
    }

    @Override
    public List<TargetUserVO> getProjectLevelTargetUser(Long projectId, String code) {
        //校验接收对象是否正确,校验三个部分组成 平台层设置，项目层设置，个人接收设置
        //1.检验项目层发送设置是否存在，不存在就返回空集合
        MessageSettingDTO messageSettingDTO = new MessageSettingDTO();
        messageSettingDTO.setCode(code);
        messageSettingDTO.setProjectId(projectId);
        MessageSettingDTO messageSettingDTO1 = messageSettingMapper.selectOne(messageSettingDTO);
        if (Objects.isNull(messageSettingDTO1)) {
            LOGGER.warn(">>>CANCEL_SENDING>>> The message setting code does not exist.[INFO:message_setting_code:'{}']", code);
            return Collections.emptyList();
        }
        //2.发送设置存在返回该设置下的接收对象
        TargetUserDTO targetUserDTO = new TargetUserDTO();
        targetUserDTO.setMessageSettingId(messageSettingDTO1.getId());
        List<TargetUserDTO> targetUserDTOS = messageSettingTargetUserMapper.select(targetUserDTO);
        if (targetUserDTOS == null || targetUserDTOS.size() == 0) {
            LOGGER.warn(">>>CANCEL_SENDING>>> The message target user does not exist.[INFO:message_target_user_code:'{}']",
                    code);
            return Collections.emptyList();
        }
        return targetUserDTOS.stream().map(e -> modelMapper.map(e, TargetUserVO.class)).collect(Collectors.toList());
    }

    @Override
    public MessageSettingVO getMessageSetting(Long projectId, String code) {
        MessageSettingDTO messageSettingDTO = new MessageSettingDTO();
        messageSettingDTO.setProjectId(projectId);
        messageSettingDTO.setCode(code);
        List<MessageSettingDTO> settingDTOS = messageSettingMapper.queryByCodeOrProjectId(messageSettingDTO);
        if (settingDTOS != null && settingDTOS.size() > 0) {
            return modelMapper.map(settingDTOS.get(0), MessageSettingVO.class);
        }
        messageSettingDTO.setProjectId(null);
        List<MessageSettingDTO> messageSettingDTOS = messageSettingMapper.queryByCodeOrProjectId(messageSettingDTO);
        if (messageSettingDTOS == null || messageSettingDTOS.size() == 0) {
            LOGGER.warn(">>>CANCEL_SENDING>>> The message setting code does not exist.[INFO:message_setting_code:'{}']", code);
            return null;
        }
        return modelMapper.map(messageSettingDTOS.get(0), MessageSettingVO.class);
    }

    @Override
    public MessageSettingWarpVO listMessageSettingByType(Long projectId, String notifyType) {
        MessageSettingWarpVO messageSettingWarpVO = new MessageSettingWarpVO();
        // 平台层没有启用任何发送设置
        List<CustomMessageSettingVO>  defaultMessageSettingList = messageSettingMapper.listDefaultAndEnabledSettingByNotifyType(notifyType);
        if (CollectionUtils.isEmpty(defaultMessageSettingList)) {
            return messageSettingWarpVO;
        }

        List<NotifyEventGroupVO> notifyEventGroupList = listEventGroupList(projectId, notifyType);
        // 资源删除验证，项目下没有启用的环境
        if (ServiceNotifyType.RESOURCE_DELETE_NOTIFY.getTypeName().equals(notifyType)
                && CollectionUtils.isEmpty(notifyEventGroupList)) {
            return messageSettingWarpVO;
        }
        messageSettingWarpVO.setNotifyEventGroupList(notifyEventGroupList);

        List<CustomMessageSettingVO> customMessageSettingList = new ArrayList<>();
        if (ServiceNotifyType.AGILE_NOTIFY.getTypeName().equals(notifyType)
                || ServiceNotifyType.DEVOPS_NOTIFY.getTypeName().equals(notifyType)) {
            customMessageSettingList = handleDevopsOrAgileSettings(defaultMessageSettingList, projectId, notifyType);
        }
        if (ServiceNotifyType.RESOURCE_DELETE_NOTIFY.getTypeName().equals(notifyType)) {
            customMessageSettingList = handleResorceDeleteSettings(notifyEventGroupList,defaultMessageSettingList, projectId, notifyType);
        }
        messageSettingWarpVO.setCustomMessageSettingList(customMessageSettingList);

        // 计算通知对象
        calculateSendRole(customMessageSettingList);
        // 添加用户信息
        addUserInfo(customMessageSettingList);
        calculateEventName(customMessageSettingList);
        return null;
    }

    @Override
    public void batchUpdateByType(Long projectId, String notifyType, List<CustomMessageSettingVO> messageSettingVOS) {
        List<CustomMessageSettingVO> defaultSettingList = messageSettingMapper.listDefaultAndEnabledSettingByNotifyType(notifyType);
        Set<Long> defaultSettingIds = defaultSettingList.stream().map(CustomMessageSettingVO::getId).collect(Collectors.toSet());
        calculateNotifyUsersByType(messageSettingVOS,notifyType);
        List<CustomMessageSettingVO> defaultMessagesSettings = messageSettingVOS.stream().filter(settingVO -> defaultSettingIds.contains(settingVO.getId())).collect(Collectors.toList());
        List<CustomMessageSettingVO> customMessagesSettings = messageSettingVOS.stream().filter(settingVO -> !defaultSettingIds.contains(settingVO.getId())).collect(Collectors.toList());
        // 默认配置的修改
        defaultMessagesSettings.forEach(settingVO -> {
            MessageSettingDTO settingDTO = modelMapper.map(settingVO, MessageSettingDTO.class);
            saveMessageSetting(settingDTO);

        });
    }

    private void calculateNotifyUsersByType(List<CustomMessageSettingVO> messageSettingVOS, String notifyType) {

            if (ServiceNotifyType.AGILE_NOTIFY.getTypeName().equals(notifyType)) {
                messageSettingVOS.forEach(settingVO -> {
                    Set<String> sendRoleList = settingVO.getSendRoleList();
                    if (!CollectionUtils.isEmpty(sendRoleList)) {
                        sendRoleList.forEach(role -> {
                            TargetUserVO targetUserVO = new TargetUserVO();
                            targetUserVO.setType(role);
                        });

                    }
                });
            }
            if (ServiceNotifyType.DEVOPS_NOTIFY.getTypeName().equals(notifyType)) {

            }
            if (ServiceNotifyType.RESOURCE_DELETE_NOTIFY.getTypeName().equals(notifyType)) {

            }

    }

    @Override
    public void saveMessageSetting(MessageSettingDTO messageSettingDTO) {
        if (messageSettingMapper.insertSelective(messageSettingDTO) != 1) {
            throw new CommonException(ERROR_SAVE_MESSAGE_SETTING);
        }
    }

    private void calculateEventName(List<CustomMessageSettingVO> customMessageSettingList) {
        customMessageSettingList.stream()
                .filter(settingVO -> RESOURCE_DELETE_CONFIRMATION.equals(settingVO.getName()))
                .forEach(settingVO -> {

        });
    }

    private void addUserInfo(List<CustomMessageSettingVO> customMessageSettingList) {
        customMessageSettingList.forEach(settingVO -> {
            Set<TargetUserVO> userList = settingVO.getUserList();
            if (!CollectionUtils.isEmpty(userList)) {
                List<Long> uids = userList.stream().map(TargetUserVO::getId).collect(Collectors.toList());
                List<UserDTO> iamUserList = baseFeignClient.listUsersByIds(uids.toArray(new Long[20]), false).getBody();
                Map<Long, UserDTO> iamUserMap = iamUserList.stream().collect(Collectors.toMap(v -> v.getId(), v -> v));
                userList.forEach(user -> {
                    UserDTO userDTO = iamUserMap.get(user.getId());
                    user.setRealName(userDTO.getRealName());
                    user.setLoginName(userDTO.getLoginName());
                });
            }
        });
    }

    private void calculateSendRole(List<CustomMessageSettingVO> customMessageSettingList) {
        customMessageSettingList.forEach(settingVO -> {
            Set<TargetUserVO> userList = settingVO.getUserList();
            if (!CollectionUtils.isEmpty(userList)) {
                Set<String> roleList = settingVO.getUserList().stream()
                        .map(user -> user.getType())
                        .collect(Collectors.toSet());
                // 设置通知角色
                settingVO.setSendRoleList(roleList);
                // 设置要通知的非指定用户
                settingVO.setUserList(userList.stream().filter(user -> TargetUserType.SPECIFIER.getTypeName().equals(user.getType())).collect(Collectors.toSet())); ;
            }
        });
    }

    private List<CustomMessageSettingVO> handleResorceDeleteSettings(List<NotifyEventGroupVO> notifyEventGroupList, List<CustomMessageSettingVO> defaultMessageSettingList, Long projectId, String notifyType) {
        List<CustomMessageSettingVO> resorceDeleteSettingList = new ArrayList<>();
        notifyEventGroupList.stream().map(NotifyEventGroupVO::getId).forEach(envId -> {
            List<CustomMessageSettingVO> customMessageSettingList = messageSettingMapper.listMessageSettingByProjectIdAndEnvId(projectId, envId, notifyType);
            Map<String, CustomMessageSettingVO> custommessageSettingVOMap = customMessageSettingList.stream().collect(Collectors.toMap(v -> v.getCode(), v -> v));
            defaultMessageSettingList.forEach(setting -> {
                if (custommessageSettingVOMap.get(setting.getCode()) == null) {
                    setting.setProjectId(projectId);
                    setting.setEnvId(envId);
                    customMessageSettingList.add(setting);
                }
            });
            resorceDeleteSettingList.addAll(customMessageSettingList);
        });
        return resorceDeleteSettingList;
    }

    private List<CustomMessageSettingVO> handleDevopsOrAgileSettings(List<CustomMessageSettingVO> defaultMessageSettingList, Long projectId, String notifyType) {
        List<CustomMessageSettingVO> customMessageSettingList = messageSettingMapper.listMessageSettingByProjectId(projectId, notifyType);
        Map<String, CustomMessageSettingVO> custommessageSettingVOMap = customMessageSettingList.stream().collect(Collectors.toMap(v -> v.getCode(), v -> v));
        defaultMessageSettingList.forEach(defaultMessageSetting -> {
            if (custommessageSettingVOMap.get(defaultMessageSetting.getCode()) == null) {
                defaultMessageSetting.setProjectId(projectId);
                customMessageSettingList.add(defaultMessageSetting);
            }
        });
        return customMessageSettingList;
    }

    private void listCustomSetting(Long projectId, String notifyType) {
        List<CustomMessageSettingVO> customMessageSettingList = messageSettingMapper.listMessageSettingByProjectId(projectId, notifyType);
    }

    private List<NotifyEventGroupVO> listEventGroupList(Long projectId, String notifyType) {
        if (ServiceNotifyType.AGILE_NOTIFY.getTypeName().equals(notifyType)
                || ServiceNotifyType.DEVOPS_NOTIFY.getTypeName().equals(notifyType)) {
             return messageSettingMapper.listCategoriesBySettingType(notifyType);
        }
        if (ServiceNotifyType.RESOURCE_DELETE_NOTIFY.getTypeName().equals(notifyType)) {
            return devopsFeginClient.listByActive(projectId, true).getBody();
        }
        return null;
    }

}
