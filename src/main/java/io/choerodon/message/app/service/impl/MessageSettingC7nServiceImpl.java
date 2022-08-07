package io.choerodon.message.app.service.impl;

import static io.choerodon.message.infra.constant.Constants.DING_TALK_SERVER_CODE;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.lov.feign.LovFeignClient;
import org.hzero.message.app.service.DingTalkServerService;
import org.hzero.message.domain.entity.DingTalkServer;
import org.hzero.message.infra.constant.HmsgConstant;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import io.choerodon.core.enums.ServiceNotifyType;
import io.choerodon.core.enums.TargetUserType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.message.api.vo.*;
import io.choerodon.message.app.eventhandler.payload.UserMemberEventPayload;
import io.choerodon.message.app.service.DingTalkServerC7nService;
import io.choerodon.message.app.service.MessageSettingC7nService;
import io.choerodon.message.app.service.MessageSettingTargetUserC7nService;
import io.choerodon.message.app.service.SendSettingC7nService;
import io.choerodon.message.infra.dto.MessageSettingDTO;
import io.choerodon.message.infra.dto.TargetUserDTO;
import io.choerodon.message.infra.dto.iam.ProjectCategoryDTO;
import io.choerodon.message.infra.dto.iam.ProjectDTO;
import io.choerodon.message.infra.dto.iam.TenantDTO;
import io.choerodon.message.infra.enums.AgileNotifyTypeEnum;
import io.choerodon.message.infra.enums.DeleteResourceType;
import io.choerodon.message.infra.enums.DevopsNotifyTypeEnum;
import io.choerodon.message.infra.enums.SendingTypeEnum;
import io.choerodon.message.infra.feign.DevopsFeignClient;
import io.choerodon.message.infra.feign.IamFeignClient;
import io.choerodon.message.infra.feign.operator.IamClientOperator;
import io.choerodon.message.infra.mapper.MessageSettingC7nMapper;
import io.choerodon.message.infra.mapper.MessageSettingTargetUserC7nMapper;
import io.choerodon.message.infra.mapper.WebHookC7nMapper;
import io.choerodon.message.infra.utils.OptionalBean;

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
    private static final String N_OPERATIONS = "N_OPERATIONS";
    private static final String STREAM_CHANGE_NOTICE = "STREAM-CHANGE-NOTICE";
    private static final String APP_SERVICE_NOTICE = "APP-SERVICE-NOTICE";
    private static final String CODE_MANAGEMENT_NOTICE = "CODE-MANAGEMENT-NOTICE";
    private static final String ORG_MESSAGE_TYPE = "healthStateChange";
    private static final String PROJECT_HEALTHSTAT_ECHANGE = "PROJECT_HEALTHSTAT_ECHANGE";
    private static final String PRODUCT_HEALTHSTAT_ECHANGE = "PRODUCT_HEALTHSTAT_ECHANGE";

    private ModelMapper modelMapper = new ModelMapper();

    private MessageSettingC7nMapper messageSettingC7nMapper;

    private MessageSettingTargetUserC7nService messageSettingTargetUserService;

    private DevopsFeignClient devopsFeignClient;

    private IamFeignClient iamFeignClient;

    private LovFeignClient lovFeignClient;

    @Autowired
    private IamClientOperator iamClientOperator;
    @Autowired
    private MessageSettingTargetUserC7nMapper messageSettingTargetUserC7nMapper;
    @Autowired
    private WebHookC7nMapper webHookC7nMapper;
    @Autowired
    @Lazy
    private SendSettingC7nService sendSettingC7nService;
    @Autowired
    private DingTalkServerC7nService dingTalkServerC7nService;
    @Autowired
    private DingTalkServerService dingTalkServerService;


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
    public List<ProjectMessageVO> listEnabledSettingByCode(String code, String notifyType) {
        List<ProjectMessageVO> result = new ArrayList<>();
        //默认配置
        List<CustomMessageSettingVO> defaultSettingList =
                messageSettingC7nMapper.listDefaultAndEnabledSettingByNotifyType(notifyType, code, ResourceLevel.PROJECT.value());
        if (defaultSettingList.isEmpty()) {
            return result;
        }
        CustomMessageSettingVO defaultSetting = defaultSettingList.get(0);
        //项目层自定义配置
        List<CustomMessageSettingVO> customSettingList =
                messageSettingC7nMapper.listMessageSettingByProjectId(null, ResourceLevel.PROJECT.value(), notifyType, code);
        buildFromMessageSetting(result, defaultSetting, customSettingList);
        addProjectFromWebHookConfig(result, code);
        return result;
    }

    private void addProjectFromWebHookConfig(List<ProjectMessageVO> result,
                                             String code) {
        Map<Long, ProjectMessageVO> projectMap =
                result
                        .stream()
                        .collect(Collectors.toMap(ProjectMessageVO::getId, Function.identity()));
        Set<Long> projectIds = webHookC7nMapper.listEnabledWebHookProjectIds(code);
        if (!projectIds.isEmpty()) {
            iamFeignClient.listProjectsByIds(projectIds)
                    .getBody()
                    .forEach(x -> {
                        if (Boolean.TRUE.equals(x.getEnabled())) {
                            Long projectId = x.getId();
                            if (ObjectUtils.isEmpty(projectMap.get(projectId))) {
                                ProjectMessageVO projectMessageVO = new ProjectMessageVO();
                                BeanUtils.copyProperties(x, projectMessageVO);
                                result.add(projectMessageVO);
                            }
                        }
                    });
        }
    }

    private void buildFromMessageSetting(List<ProjectMessageVO> result,
                                         CustomMessageSettingVO defaultSetting,
                                         List<CustomMessageSettingVO> customSettingList) {
        boolean doSendMsg =
                Boolean.TRUE.equals(defaultSetting.getPmEnable())
                        || Boolean.TRUE.equals(defaultSetting.getEmailEnable())
                        || Boolean.TRUE.equals(defaultSetting.getDtEnable());
        List<ProjectVO> projects = new ArrayList<>();
        Map<Long, CustomMessageSettingVO> projectSettingMap = new HashMap<>();
        if (doSendMsg) {
            List<ProjectVO> projectList = iamFeignClient.listAllProjects(true).getBody();
            Set<Long> skipProjectIds = new HashSet<>();
            customSettingList.forEach(x -> {
                Long projectId = x.getProjectId();
                if (Objects.equals(projectId, 0L)) {
                    return;
                }
                projectSettingMap.put(projectId, x);
                if (Boolean.FALSE.equals(x.getPmEnable())
                        && Boolean.FALSE.equals(x.getEmailEnable())
                        && Boolean.FALSE.equals(x.getDtEnable())) {
                    skipProjectIds.add(x.getProjectId());
                }
            });
            projects.addAll(
                    projectList
                            .stream()
                            .filter(x -> !skipProjectIds.contains(x.getId()))
                            .collect(Collectors.toList()));
        } else {
            Set<Long> projectIds = new HashSet<>();
            customSettingList.forEach(x -> {
                Long projectId = x.getProjectId();
                if (Objects.equals(projectId, 0L)) {
                    return;
                }
                if (Boolean.FALSE.equals(x.getPmEnable())
                        && Boolean.FALSE.equals(x.getEmailEnable())
                        && Boolean.FALSE.equals(x.getDtEnable())) {
                    return;
                }
                projectSettingMap.put(projectId, x);
                projectIds.add(projectId);
            });
            List<ProjectDTO> projectList = new ArrayList<>();
            if (!projectIds.isEmpty()) {
                projectList.addAll(
                        iamFeignClient.listProjectsByIds(projectIds)
                                .getBody()
                                .stream()
                                .filter(x -> Boolean.TRUE.equals(x.getEnabled()))
                                .collect(Collectors.toList()));
            }
            projects.addAll(
                    modelMapper.map(projectList,
                            new TypeToken<List<ProjectVO>>() {
                            }.getType()));
        }

        buildProjectMessageVO(result, defaultSetting, projects, projectSettingMap);
    }

    private void buildProjectMessageVO(List<ProjectMessageVO> result,
                                       CustomMessageSettingVO defaultSetting,
                                       List<ProjectVO> projects,
                                       Map<Long, CustomMessageSettingVO> projectSettingMap) {
        projects.forEach(x -> {
            Long projectId = x.getId();
            ProjectMessageVO projectMessageVO = new ProjectMessageVO();
            result.add(projectMessageVO);
            BeanUtils.copyProperties(x, projectMessageVO);
            CustomMessageSettingVO customMessageSettingVO = projectSettingMap.get(projectId);
            List<TargetUserVO> targetUserList;
            if (customMessageSettingVO != null) {
                targetUserList = customMessageSettingVO.getUserList();
                projectMessageVO.setEmailEnable(customMessageSettingVO.getEmailEnable());
                projectMessageVO.setPmEnable(customMessageSettingVO.getPmEnable());
                projectMessageVO.setDtEnable(customMessageSettingVO.getDtEnable());
                projectMessageVO.setSmsEnable(customMessageSettingVO.getSmsEnable());
            } else {
                targetUserList = defaultSetting.getUserList();
                projectMessageVO.setEmailEnable(defaultSetting.getEmailEnable());
                projectMessageVO.setPmEnable(defaultSetting.getPmEnable());
                projectMessageVO.setDtEnable(defaultSetting.getDtEnable());
                projectMessageVO.setSmsEnable(defaultSetting.getSmsEnable());
            }
            Set<Long> userIds = new HashSet<>();
            Set<String> receiverTypes = new HashSet<>();
            projectMessageVO.setUserIds(userIds);
            projectMessageVO.setReceiverTypes(receiverTypes);
            if (!ObjectUtils.isEmpty(targetUserList)) {
                targetUserList.forEach(y -> {
                    receiverTypes.add(y.getType());
                    Long userId = y.getUserId();
                    if (userId != null
                            && !Objects.equals(userId, 0L)) {
                        userIds.add(userId);
                    }
                });
            }
        });
    }

    @Override
    public MessageSettingWarpVO listMessageSettingByType(Long projectId, String notifyType, String eventName) {
        MessageSettingWarpVO messageSettingWarpVO = new MessageSettingWarpVO();
        //查询平台层的发送设置
        List<CustomMessageSettingVO> defaultMessageSettings = messageSettingC7nMapper.listDefaultSettingByNotifyType(notifyType, ResourceLevel.PROJECT.value());
        if (CollectionUtils.isEmpty(defaultMessageSettings)) {
            return messageSettingWarpVO;
        }

        //查询通知对象
        List<CustomMessageSettingVO> defaultMessageSettingList = messageSettingC7nMapper.listDefaultAndEnabledSettingByNotifyType(notifyType, ResourceLevel.PROJECT.value(), null);
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
        //运维项目去掉开发相关的通知
        ProjectDTO projectDTO = iamClientOperator.queryProjectById(projectId);
        if (!CollectionUtils.isEmpty(projectDTO.getCategories()) && projectDTO.getCategories().stream().map(ProjectCategoryDTO::getCode).collect(Collectors.toList()).contains(N_OPERATIONS)) {
            notifyEventGroupList = notifyEventGroupList.stream().filter(notifyEventGroupVO ->
                    !StringUtils.equalsIgnoreCase(notifyEventGroupVO.getCategoryCode(), STREAM_CHANGE_NOTICE)
                            && !StringUtils.equalsIgnoreCase(notifyEventGroupVO.getCategoryCode(), APP_SERVICE_NOTICE)
                            && !StringUtils.equalsIgnoreCase(notifyEventGroupVO.getCategoryCode(), CODE_MANAGEMENT_NOTICE)
            ).collect(Collectors.toList());
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
            if (SendingTypeEnum.DT.getValue().equals(customMessageSettingVO.getNotifyType()) && customMessageSettingVO.getEnabledFlag()) {
                customMessageSettingVO.setDtEnabledFlag(Boolean.TRUE);
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
                if (SendingTypeEnum.DT.getValue().equals(customMessageSettingVO1.getSendingType())
                        && customMessageSettingVO1.getCode().equals(customMessageSettingVO.getCode())
                        && customMessageSettingVO1.getEnabledFlag()) {
                    customMessageSettingVO.setDtEnabledFlag(Boolean.TRUE);
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
        List<CustomMessageSettingVO> defaultSettingList = messageSettingC7nMapper.listDefaultAndEnabledSettingByNotifyType(notifyType, ResourceLevel.PROJECT.value(), null);
        Set<Long> defaultSettingIds = defaultSettingList.stream().map(CustomMessageSettingVO::getId).collect(Collectors.toSet());
        // 将非指定用户添加到userList
        calculateNotifyUsersByType(messageSettingVOS);
        List<CustomMessageSettingVO> defaultMessagesSettings = messageSettingVOS.stream().filter(settingVO -> defaultSettingIds.contains(settingVO.getId())).collect(Collectors.toList());
        List<CustomMessageSettingVO> customMessagesSettings = messageSettingVOS.stream().filter(settingVO -> !defaultSettingIds.contains(settingVO.getId())).collect(Collectors.toList());
        // 默认配置的修改
        defaultMessagesSettings.forEach(settingVO -> {
            MessageSettingDTO settingDTO = modelMapper.map(settingVO, MessageSettingDTO.class);
            settingDTO.setSourceId(settingVO.getProjectId());
            settingDTO.setId(null);
            saveMessageSetting(settingDTO);
            List<TargetUserVO> userList = settingVO.getUserList();
            if (!CollectionUtils.isEmpty(userList)) {
                settingVO.getUserList().forEach(user -> {
                    user.setMessageSettingId(settingDTO.getId());
                    messageSettingTargetUserService.save(modelMapper.map(user, TargetUserDTO.class));
                });
            }
        });
        // 自定配置修改
        customMessagesSettings.forEach(settingVO -> {
            MessageSettingDTO settingDTO = modelMapper.map(settingVO, MessageSettingDTO.class);
            settingDTO.setSourceId(settingVO.getProjectId());
            updateMessageSetting(settingDTO);
            // devops消息不更新通知对象
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
            messageSettingVO = messageSettingC7nMapper.getResourceDeleteSettingByOption(notifyType, projectId, ResourceLevel.PROJECT.value(), code, envId, eventName);
            if (messageSettingVO == null) {
                messageSettingVO = messageSettingC7nMapper.getDefaultResourceDeleteSetting(notifyType, code, eventName);
            }
        } else {
            messageSettingVO = messageSettingC7nMapper.getSettingByTypeAndCode(notifyType, projectId, ResourceLevel.PROJECT.value(), code);
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
        messageSettingDTO.setSourceId(TenantDTO.DEFAULT_TENANT_ID);
        // TODO: 2022/8/2   sourceLevel
        messageSettingC7nMapper.updateOptional(messageSettingDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void asyncMessageProjectUser(List<UserMemberEventPayload> userMemberEventPayloads) {
        if (CollectionUtils.isEmpty(userMemberEventPayloads)) {
            return;
        }
        userMemberEventPayloads.forEach(userMemberEventPayload -> {
            handUserMember(userMemberEventPayload);
        });
    }

    @Override
    public void insertOpenAppConfig(OpenAppVO openAppVO) {
        if ("ding_talk".equals(openAppVO.getType())) {
            TenantDTO tenantDTO = iamClientOperator.queryTenantById(openAppVO.getTenantId());
            DingTalkServer dingTalkServer = new DingTalkServer();
            dingTalkServer.setAppKey(openAppVO.getAppId());
            dingTalkServer.setAppSecret(openAppVO.getAppSecret());
            dingTalkServer.setTenantId(openAppVO.getTenantId());
            dingTalkServer.setServerName(tenantDTO.getTenantName());
            dingTalkServer.setTenantName(tenantDTO.getTenantName());
            dingTalkServer.setAgentId(Long.parseLong(openAppVO.getAgentId()));
            dingTalkServer.setEnabledFlag(1);
            dingTalkServer.setAuthType("DingTalk");
            dingTalkServer.setServerCode(DING_TALK_SERVER_CODE);
            dingTalkServerC7nService.addDingTalkServer(openAppVO.getTenantId(), dingTalkServer);
        }
    }

    @Override
    public void updateOpenAppConfig(OpenAppVO openAppVO) {
        if ("ding_talk".equals(openAppVO.getType())) {
            TenantDTO tenantDTO = iamClientOperator.queryTenantById(openAppVO.getTenantId());
            DingTalkServer dingTalkServer = dingTalkServerService.getConfigWithDb(openAppVO.getTenantId(), DING_TALK_SERVER_CODE);
            if (dingTalkServer == null) {
                insertOpenAppConfig(openAppVO);
            }
            dingTalkServer.setAppKey(openAppVO.getAppId());
            dingTalkServer.setAppSecret(openAppVO.getAppSecret());
            dingTalkServer.setTenantId(openAppVO.getTenantId());
            dingTalkServer.setServerName(tenantDTO.getTenantName());
            dingTalkServer.setTenantName(tenantDTO.getTenantName());
            dingTalkServer.setAgentId(Long.parseLong(openAppVO.getAgentId()));
            dingTalkServer.setEnabledFlag(1);
            dingTalkServer.setAuthType("DingTalk");
            dingTalkServer.setServerCode(DING_TALK_SERVER_CODE);
            dingTalkServerC7nService.updateDingTalkServer(openAppVO.getTenantId(), dingTalkServer);
        }
    }

    @Override
    public void enableOrDisableOpenAppSyncSetting(OpenAppVO openAppVO) {
        if ("ding_talk".equals(openAppVO.getType())) {
            TenantDTO tenantDTO = iamClientOperator.queryTenantById(openAppVO.getTenantId());
            DingTalkServer dingTalkServer = dingTalkServerService.getConfigWithDb(openAppVO.getTenantId(), DING_TALK_SERVER_CODE);
            dingTalkServer.setAppKey(openAppVO.getAppId());
            dingTalkServer.setAppSecret(openAppVO.getAppSecret());
            dingTalkServer.setTenantId(openAppVO.getTenantId());
            dingTalkServer.setServerName(tenantDTO.getTenantName());
            dingTalkServer.setTenantName(tenantDTO.getTenantName());
            dingTalkServer.setEnabledFlag(openAppVO.getEnabledFlag() ? 1 : 0);
            dingTalkServer.setAuthType("DingTalk");
            dingTalkServer.setServerCode(DING_TALK_SERVER_CODE);
            dingTalkServerC7nService.updateDingTalkServer(openAppVO.getTenantId(), dingTalkServer);
        }
    }

    @Override
    public MessageSettingWarpVO queryOrgMessageSettings(Long organizationId, String notifyType) {
        MessageSettingWarpVO messageSettingWarpVO = new MessageSettingWarpVO();
        //查询平台层的发送设置
        List<CustomMessageSettingVO> defaultMessageSettings = messageSettingC7nMapper.listDefaultSettingByNotifyType(notifyType, ResourceLevel.ORGANIZATION.value());
        if (CollectionUtils.isEmpty(defaultMessageSettings)) {
            return messageSettingWarpVO;
        }
        //查询通知对象
        List<CustomMessageSettingVO> defaultMessageSettingList = messageSettingC7nMapper.listDefaultAndEnabledSettingByNotifyType(notifyType, ResourceLevel.ORGANIZATION.value(), null);
        assemblingSendSetting(defaultMessageSettingList, defaultMessageSettings);

        // 计算平台层是否启用发送短信，站内信，邮件
        calculateStieSendSetting(defaultMessageSettingList);
        List<NotifyEventGroupVO> notifyEventGroupList = listEventGroupList(organizationId, notifyType);

        List<CustomMessageSettingVO> customMessageSettingList = handleHealthStateSettings(defaultMessageSettingList, organizationId, notifyType);

        customMessageSettingList.stream().map(customMessageSettingVO -> {
            String lovCode = customMessageSettingVO.getSubcategoryCode();
            customMessageSettingVO.setGroupId(lovCode);
            //这里角色前端说不好接口请求拼接，所以后端返回
            if (StringUtils.equalsIgnoreCase(customMessageSettingVO.getCode(), PROJECT_HEALTHSTAT_ECHANGE)) {
                //可选项为你组织层角色，项目层角色
                List<Role> roles = iamClientOperator.queryRoleCodeByTenantId(organizationId);
                customMessageSettingVO.setSendTargetRole(roles);
            }
            if (StringUtils.equalsIgnoreCase(customMessageSettingVO.getCode(), PRODUCT_HEALTHSTAT_ECHANGE)) {

                //产品负责人
                Role role = new Role();
                role.setCode("product-owner");
                role.setName("产品负责人");
                //组织层角色
                List<Role> roleList = iamClientOperator.queryTenantRoleCodeByTenantId(organizationId, "organization");
                List<Role> reRoleList = new ArrayList<>();
                reRoleList.add(role);
                if (!CollectionUtils.isEmpty(roleList)) {
                    reRoleList.addAll(roleList);
                }
                customMessageSettingVO.setSendTargetRole(reRoleList);
            }
            return customMessageSettingVO;
        }).collect(Collectors.toList());

        // 计算通知对象
        calculateSendRole(customMessageSettingList);
        // 添加用户信息
        addUserInfo(customMessageSettingList);
        // 计算通知事件的名称
        calculateEventName(customMessageSettingList);
        //排序
        List<CustomMessageSettingVO> reCustomMessageSettingVOS = customMessageSettingList.stream().sorted(Comparator.comparing(CustomMessageSettingVO::getId)).collect(Collectors.toList());
        // 装配VO
        messageSettingWarpVO.setCustomMessageSettingList(sortEvent(notifyType, reCustomMessageSettingVOS));
        messageSettingWarpVO.setNotifyEventGroupList(notifyEventGroupList);
        return messageSettingWarpVO;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchOrgUpdateByType(Long organizationId, String notifyType, List<CustomMessageSettingVO> messageSettingVOS) {
        if (messageSettingVOS.stream().anyMatch(settingVO -> !notifyType.equals(settingVO.getNotifyType()))) {
            throw new CommonException(ERROR_PARAM_INVALID);
        }
        List<CustomMessageSettingVO> defaultSettingList = messageSettingC7nMapper.listDefaultAndEnabledSettingByNotifyType(notifyType, ResourceLevel.ORGANIZATION.value(), null);
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
            List<TargetUserVO> userList = settingVO.getUserList();
            if (!CollectionUtils.isEmpty(userList)) {
                settingVO.getUserList().forEach(user -> {
                    user.setMessageSettingId(settingDTO.getId());
                    messageSettingTargetUserService.save(modelMapper.map(user, TargetUserDTO.class));
                });
            }
        });
        // 自定配置修改
        customMessagesSettings.forEach(settingVO -> {
            MessageSettingDTO settingDTO = modelMapper.map(settingVO, MessageSettingDTO.class);
            updateMessageSetting(settingDTO);
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
        });
    }

    @Override
    public MessageSettingVO getSettingByCode(Long sourceId, String notifyType, String code) {
        if (StringUtils.equalsIgnoreCase(notifyType, ORG_MESSAGE_TYPE)) {
            MessageSettingVO settingVO = messageSettingC7nMapper.getSettingByTypeAndCode(notifyType, sourceId, ResourceLevel.ORGANIZATION.value(), code);
            if (settingVO == null) {
                return messageSettingC7nMapper.getDefaultProjectHealthSetting(notifyType, code);
            } else {
                return settingVO;
            }
        }
        return new MessageSettingVO();
    }

    private List<CustomMessageSettingVO> handleHealthStateSettings(List<CustomMessageSettingVO> defaultMessageSettingList, Long sourceId, String notifyType) {
        List<CustomMessageSettingVO> customMessageSettingList = messageSettingC7nMapper.listMessageSettingByProjectId(sourceId, ResourceLevel.ORGANIZATION.value(), notifyType, null);
        Map<String, CustomMessageSettingVO> customMessageSettingVOMap = customMessageSettingList.stream().collect(Collectors.toMap(CustomMessageSettingVO::getCode, v -> v));
        defaultMessageSettingList.forEach(defaultMessageSetting -> {
            CustomMessageSettingVO customMessageSettingVO = customMessageSettingVOMap.get(defaultMessageSetting.getCode());
            if (customMessageSettingVO == null) {
                defaultMessageSetting.setSourceId(sourceId);
                customMessageSettingList.add(defaultMessageSetting);
            } else {
                customMessageSettingVO.setSmsEnabledFlag(defaultMessageSetting.getSmsEnabledFlag());
                customMessageSettingVO.setEmailEnabledFlag(defaultMessageSetting.getEmailEnabledFlag());
                customMessageSettingVO.setDtEnabledFlag(defaultMessageSetting.getDtEnabledFlag());
                customMessageSettingVO.setPmEnabledFlag(defaultMessageSetting.getPmEnabledFlag());
            }
        });
        return customMessageSettingList;
    }

    private void handUserMember(UserMemberEventPayload userMemberEventPayload) {
        //如果用户在这个项目下没有任何角色，那么删除他在通知里面的对象
        if (!userMemberEventPayload.getResourceType().equals(ResourceLevel.PROJECT.value())) {
            return;
        }
        UserVO user = iamClientOperator.getUser(userMemberEventPayload.getResourceId(), userMemberEventPayload.getUsername());
        boolean present = OptionalBean.ofNullable(user).getBean(UserVO::getRoles).isPresent();
        //user存在的时候返回
        if (present) {
            return;
        }
        //清理项目层通知对象
        MessageSettingDTO messageSettingDTO = new MessageSettingDTO();
        // TODO: 2022/8/2 sourceLevel
        messageSettingDTO.setSourceId(userMemberEventPayload.getResourceId());
        List<MessageSettingDTO> messageSettingDTOS = messageSettingC7nMapper.select(messageSettingDTO);
        if (CollectionUtils.isEmpty(messageSettingDTOS)) {
            return;
        }
        messageSettingDTOS.forEach(settingDTO -> {
            TargetUserDTO targetUserDTO = new TargetUserDTO();
            targetUserDTO.setMessageSettingId(settingDTO.getId());
            targetUserDTO.setUserId(userMemberEventPayload.getUserId());
            targetUserDTO.setType(TargetUserType.SPECIFIER.getTypeName());
            messageSettingTargetUserC7nMapper.delete(targetUserDTO);
        });
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
        return notifyEventGroupList.stream().filter(group -> groupIds.contains(String.valueOf(group.getId()))).collect(Collectors.toList());
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
                if (SendingTypeEnum.DT.getValue().equals(settingVO.getSendSetting().getSendingType().trim())) {
                    settingVO.setDtEnabledFlag(true);
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
                Long[] animals = userList.stream().map(TargetUserVO::getUserId).distinct().toArray(Long[]::new);
                List<UserVO> iamUserList = iamFeignClient.listUsersByIds(animals, false).getBody();
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
                if (ServiceNotifyType.DEVOPS_NOTIFY.getTypeName().equals(settingVO.getNotifyType())) {
                    List<String> stringList = settingVO.getUserList().stream().map(t -> TargetUserType.nameMapping.get(t.getType())).collect(Collectors.toList());
                    settingVO.setNotifyObject(StringUtils.join(stringList, ","));
                }
                // 设置通知角色
                settingVO.setSendRoleList(roleList);
                // 设置要通知的非指定用户
                settingVO.setUserList(userList.stream().filter(user -> TargetUserType.SPECIFIER.getTypeName().equals(user.getType())).collect(Collectors.toList()));
                settingVO.setSpecifierIds(settingVO.getUserList().stream().map(TargetUserVO::getUserId).collect(Collectors.toSet()));

            }
        });
    }

    private List<CustomMessageSettingVO> handleResorceDeleteSettings(List<NotifyEventGroupVO> notifyEventGroupList, List<CustomMessageSettingVO> defaultMessageSettingList, Long projectId, String notifyType) {
        List<CustomMessageSettingVO> resorceDeleteSettingList = new ArrayList<>();
        notifyEventGroupList.stream().map(NotifyEventGroupVO::getId).forEach(envId -> {
            List<CustomMessageSettingVO> customMessageSettingList = messageSettingC7nMapper.listMessageSettingByProjectIdAndEnvId(projectId, ResourceLevel.PROJECT.value(), envId, notifyType);
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
                    customMessageSettingVO.setDtEnabledFlag(setting.getDtEnabledFlag());
                    customMessageSettingVO.setPmEnabledFlag(setting.getPmEnabledFlag());
                }
            });
            resorceDeleteSettingList.addAll(customMessageSettingList);
        });
        resorceDeleteSettingList.forEach(settingVO -> settingVO.setGroupId(settingVO.getEnvId().toString()));
        return resorceDeleteSettingList;
    }

    private List<CustomMessageSettingVO> handleDevopsOrAgileSettings(List<CustomMessageSettingVO> defaultMessageSettingList, Long projectId, String notifyType) {
        List<CustomMessageSettingVO> customMessageSettingList = messageSettingC7nMapper.listMessageSettingByProjectId(projectId, ResourceLevel.PROJECT.value(), notifyType, null);
        Map<String, CustomMessageSettingVO> customMessageSettingVOMap = customMessageSettingList.stream().collect(Collectors.toMap(CustomMessageSettingVO::getCode, v -> v));
        defaultMessageSettingList.forEach(defaultMessageSetting -> {
            CustomMessageSettingVO customMessageSettingVO = customMessageSettingVOMap.get(defaultMessageSetting.getCode());
            if (customMessageSettingVO == null) {
                defaultMessageSetting.setProjectId(projectId);
                customMessageSettingList.add(defaultMessageSetting);
            } else {
                customMessageSettingVO.setSmsEnabledFlag(defaultMessageSetting.getSmsEnabledFlag());
                customMessageSettingVO.setEmailEnabledFlag(defaultMessageSetting.getEmailEnabledFlag());
                customMessageSettingVO.setDtEnabledFlag(defaultMessageSetting.getDtEnabledFlag());
                customMessageSettingVO.setPmEnabledFlag(defaultMessageSetting.getPmEnabledFlag());
            }
        });
        return customMessageSettingList;
    }

    private List<NotifyEventGroupVO> listEventGroupList(Long projectId, String notifyType) {
        if (ServiceNotifyType.AGILE_NOTIFY.getTypeName().equals(notifyType)
                || ServiceNotifyType.DEVOPS_NOTIFY.getTypeName().equals(notifyType)
                || "healthStateChange".equals(notifyType)) {
            List<NotifyEventGroupVO> notifyEventGroupVOS = new ArrayList<>();
            List<String> stringList = messageSettingC7nMapper.listCategoryCode(notifyType);
            if (CollectionUtils.isEmpty(stringList)) {
                return Collections.emptyList();
            }
            stringList.forEach(s -> {
                NotifyEventGroupVO notifyEventGroupVO = new NotifyEventGroupVO();
                notifyEventGroupVO.setCategoryCode(s);
                Map<String, Map<String, String>> meaningsMap = sendSettingC7nService.getMeanings();
                notifyEventGroupVO.setName(meaningsMap.get(ResourceLevel.PROJECT.name()).get(s));
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
