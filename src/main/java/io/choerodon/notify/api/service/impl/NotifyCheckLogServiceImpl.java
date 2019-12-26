package io.choerodon.notify.api.service.impl;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.alibaba.fastjson.JSON;
import com.zaxxer.hikari.util.UtilityElf;
import io.choerodon.core.notify.ServiceNotifyType;
import io.choerodon.notify.infra.enums.DeleteResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.notify.TargetUserType;
import io.choerodon.notify.api.dto.CheckLog;
import io.choerodon.notify.api.dto.DevopsNotificationTransferDataVO;
import io.choerodon.notify.api.dto.MessageDetailDTO;
import io.choerodon.notify.api.service.NotifyCheckLogService;
import io.choerodon.notify.infra.dto.MessageSettingDTO;
import io.choerodon.notify.infra.dto.NotifyCheckLogDTO;
import io.choerodon.notify.infra.dto.SendSettingDTO;
import io.choerodon.notify.infra.dto.TargetUserDTO;
import io.choerodon.notify.infra.feign.AgileFeignClient;
import io.choerodon.notify.infra.feign.DevopsFeginClient;
import io.choerodon.notify.infra.mapper.MessageSettingMapper;
import io.choerodon.notify.infra.mapper.MessageSettingTargetUserMapper;
import io.choerodon.notify.infra.mapper.NotifyCheckLogMapper;
import io.choerodon.notify.infra.mapper.SendSettingMapper;


@Service
public class NotifyCheckLogServiceImpl implements NotifyCheckLogService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotifyCheckLogServiceImpl.class);
    private static final String AGILE = "agile";
    private static final ExecutorService executorService = new ThreadPoolExecutor(0, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), new UtilityElf.DefaultThreadFactory("notify-upgrade", false));

    private static final Map<String, String> codeMap = new HashMap<>(3);
    private static final Map<String, String> targetUserType = new HashMap<>(5);

    static {
        codeMap.put("issue_assigneed", "issueAssignee");
        codeMap.put("issue_solved", "issueSolve");
        codeMap.put("issue_created", "issueCreate");

        targetUserType.put("assigneer", "assignee");
        targetUserType.put("reporter", "reporter");
        targetUserType.put("users", "specifier");
        targetUserType.put("project_owner", "projectOwner");
    }

    @Autowired
    private NotifyCheckLogMapper notifyCheckLogMapper;
    @Autowired
    private AgileFeignClient agileFeignClient;
    @Autowired
    private SendSettingMapper sendSettingMapper;
    @Autowired
    private MessageSettingMapper messageSettingMapper;
    @Autowired
    private MessageSettingTargetUserMapper messageSettingTargetUserMapper;
    @Autowired
    private DevopsFeginClient devopsFeignClient;

    @Override
    public void checkLog(String version, String type) {
        LOGGER.info("start upgrade task");
        executorService.execute(new UpgradeTask(version, type));
    }

    class UpgradeTask implements Runnable {
        private String version;
        private String type;

        UpgradeTask(String version, String type) {
            this.version = version;
            this.type = type;
        }

        @Override
        public void run() {
            try {
                NotifyCheckLogDTO notifyCheckLogDTO = new NotifyCheckLogDTO();
                List<CheckLog> logs = new ArrayList<>();
                notifyCheckLogDTO.setBeginCheckDate(new Date());
                if ("0.20.0".equals(version) && type.equals("devops")) {
                    transferDevopsData(logs);
                } else if ("0.20.0".equals(version) && type.equals("agile")) {
                    syncAgileNotify(logs);
                } else if ("0.20.0".equals(version) && type.equals("notify")) {
                    syncNotifySendSetting(logs);
                } else {
                    LOGGER.info("version not matched");
                }
                if("0.21.0".equals(version) && type.equals("notify")) {
                    initTargetUser();
                }


                notifyCheckLogDTO.setLog(JSON.toJSONString(logs));
                notifyCheckLogDTO.setEndCheckDate(new Date());
                notifyCheckLogMapper.insert(notifyCheckLogDTO);
            } catch (Throwable ex) {
                LOGGER.warn("Exception occurred when applying data migration. The ex is: {}", ex);
            }
        }
    }
    private void initTargetUser() {
        // 初始化资源删除验证通知对象

        // 1.获取资源删除默认事件
        MessageSettingDTO record = new MessageSettingDTO();
        record.setNotifyType(ServiceNotifyType.RESOURCE_DELETE_NOTIFY.getTypeName());
        record.setProjectId(0L);
        record.setCode("resourceDeleteConfirmation");
        List<MessageSettingDTO> messageSettingDTOList = messageSettingMapper.select(record);
        messageSettingDTOList.forEach(setting -> {
            // 2.初始化通知对象
            TargetUserDTO targetUserDTO = new TargetUserDTO();
            targetUserDTO.setMessageSettingId(setting.getId());
            targetUserDTO.setType(DeleteResourceType.notifyTargetMapping.get(setting.getEventName()));
            targetUserDTO.setUserId(0L);
            messageSettingTargetUserMapper.insertSelective(targetUserDTO);
        });

    }
    private void syncAgileNotify(List<CheckLog> logs) {
        LOGGER.info("begin to sync agile notify!");
        List<MessageDetailDTO> messageDetailDTOList;
        try {
            messageDetailDTOList = agileFeignClient.migrateMessageDetail().getBody();
        } catch (Exception e) {
            CheckLog checkLog = new CheckLog();
            checkLog.setContent("Get message of agile!");
            checkLog.setResult("error.get.agile.message.detail");
            logs.add(checkLog);
            throw new CommonException("error.get.agile.message.detail", e);
        }

        if (messageDetailDTOList != null && messageDetailDTOList.size() > 0) {
            Map<Long, List<MessageDetailDTO>> messageDetailMap = messageDetailDTOList.stream().collect(Collectors.groupingBy(MessageDetailDTO::getProjectId));
            for (Map.Entry<Long, List<MessageDetailDTO>> map : messageDetailMap.entrySet()) {
                CheckLog checkLog = new CheckLog();
                checkLog.setContent("begin to sync agile notify projectId:" + map.getKey());
                try {
                    for (MessageDetailDTO v : map.getValue()) {
                        MessageSettingDTO messageSettingDTO = new MessageSettingDTO();
                        messageSettingDTO.setProjectId(map.getKey());
                        messageSettingDTO.setNotifyType(AGILE);
                        messageSettingDTO.setCode(codeMap.get(v.getEvent()));
                        // 1. messageSetting
                        MessageSettingDTO queryDTO = messageSettingMapper.selectOne(messageSettingDTO);
                        if (queryDTO == null) {
                            messageSettingDTO.setPmEnable(true);
                            if (messageSettingMapper.insertSelective(messageSettingDTO) != 1) {
                                throw new CommonException("error.insert.message.send.setting");
                            }
                        } else {
                            messageSettingDTO.setId(queryDTO.getId());
                        }
                        //2. messageSettingTargetUser
                        if (v.getEnable()) {
                            if (v.getNoticeType().equals("users")) {
                                if (v.getUser() == null || v.getUser().equals("")) {
                                    continue;
                                }
                                String[] userIds = v.getUser().split(",");
                                for (String userId : userIds) {
                                    if (!userId.equals("") && isInteger(userId)) {
                                        createMessageSettingTargetUser(messageSettingDTO.getId(), targetUserType.get(v.getNoticeType()), Long.valueOf(userId));
                                    }
                                }
                            } else {
                                createMessageSettingTargetUser(messageSettingDTO.getId(), targetUserType.get(v.getNoticeType()), null);
                            }
                        }
                        checkLog.setResult("Succeed to sync agile notify!");
                    }
                } catch (Exception e) {
                    checkLog.setResult("Failed to sync agile notify!");
                    throw e;
                }
                logs.add(checkLog);
            }
        }

    }

    private void transferDevopsData(List<CheckLog> logs) {
        List<DevopsNotificationTransferDataVO> devopsNotificationVOS;
        try {
            devopsNotificationVOS = devopsFeignClient.transferData(1L).getBody();
        } catch (Exception e) {
            CheckLog checkLog = new CheckLog();
            checkLog.setContent("Get message of devops!");
            checkLog.setResult("error.get.devops.message.data");
            logs.add(checkLog);
            throw new CommonException("error.get.devops.message.data", e);
        }
        if (devopsNotificationVOS != null || devopsNotificationVOS.size() > 0) {
            Map<Long, List<DevopsNotificationTransferDataVO>> longListMap = devopsNotificationVOS
                    .stream()
                    .collect(Collectors.groupingBy(DevopsNotificationTransferDataVO::getProjectId));
            for (Map.Entry<Long, List<DevopsNotificationTransferDataVO>> longListEntry : longListMap.entrySet()) {
                CheckLog checkLog = new CheckLog();
                checkLog.setContent("begin to sync devops notify projectId:" + longListEntry.getKey());
                try {
                    for (DevopsNotificationTransferDataVO devopsNotificationTransferDataVO : longListEntry.getValue()) {
                        MessageSettingDTO messageSettingDTO = new MessageSettingDTO();
                        messageSettingDTO.setCode("resourceDeleteConfirmation");
                        messageSettingDTO.setProjectId(Objects.isNull(devopsNotificationTransferDataVO.getProjectId()) ? null : devopsNotificationTransferDataVO.getProjectId());
                        messageSettingDTO.setNotifyType("resourceDelete");
                        messageSettingDTO.setEnvId(devopsNotificationTransferDataVO.getEnvId());
                        List<String> recouseNameList = new ArrayList<>();
                        List<String> notifyType = new ArrayList<>();
                        recouseNameList = fillName(recouseNameList, devopsNotificationTransferDataVO);
                        notifyType = fillType(notifyType, devopsNotificationTransferDataVO);
                        for (String name : recouseNameList) {
                            messageSettingDTO.setEventName(name);
                            MessageSettingDTO condition = new MessageSettingDTO();
                            condition.setEventName(name);
                            condition.setEnvId(devopsNotificationTransferDataVO.getEnvId());
                            condition.setProjectId(devopsNotificationTransferDataVO.getProjectId());
                            MessageSettingDTO queryDTO = messageSettingMapper.selectOne(condition);
                            if (queryDTO == null) {
                                fillMessageSettings(messageSettingDTO, notifyType);
                                messageSettingDTO.setId(null);
                                //插入messageSetting
                                if (messageSettingMapper.insertSelective(messageSettingDTO) != 1) {
                                    throw new CommonException("error.insert.message.send.setting");
                                }
                                //插入接收对象
                                saveMessageSettingTargetUser(devopsNotificationTransferDataVO, messageSettingDTO);
                            } else {
                                messageSettingDTO.setId(queryDTO.getId());
                                //插入接收对象
                                saveMessageSettingTargetUser(devopsNotificationTransferDataVO, messageSettingDTO);
                            }
                        }
                        recouseNameList.clear();
                        notifyType.clear();
                        checkLog.setResult("Succeed to sync devops notify!");
                    }
                } catch (Exception e) {
                    checkLog.setResult("Failed to sync devops notify!");
                    throw e;
                }
                logs.add(checkLog);
            }
        }
    }

    private void syncNotifySendSetting(List<CheckLog> logs) {
        List<String> deleteNotifyList = new ArrayList<>();
        deleteNotifyList.add("registerOrganization");
        deleteNotifyList.add("jobStatusProject");
        deleteNotifyList.add("buzz-reply-message");
        deleteNotifyList.add("buzz-message");
        deleteNotifyList.add("captcha-registrantOrganization");
        deleteNotifyList.add(" registrant-exception");
        deleteNotifyList.add("registerOrganization-expireSoon");
        deleteNotifyList.add("registerOrganization-expired");
        deleteNotifyList.add("registrant-exception");
        deleteNotifyList.add("marketApplicationNotice-base");
        deleteNotifyList.add("devopsDeleteInstance4Sms");

        CheckLog checkLog = new CheckLog();
        checkLog.setContent("begin to sync notify!");

        deleteNotifyList.forEach(code -> {
            SendSettingDTO sendSettingDTO = new SendSettingDTO();
            sendSettingDTO.setCode(code);
            sendSettingMapper.delete(sendSettingDTO);
        });
        checkLog.setResult("success");
        logs.add(checkLog);
    }

    private void createMessageSettingTargetUser(Long messageSettingId, String noticeType, Long userId) {
        TargetUserDTO targetUserDTO = new TargetUserDTO();
        targetUserDTO.setMessageSettingId(messageSettingId);
        targetUserDTO.setType(noticeType);
        targetUserDTO.setUserId(userId);
        if (messageSettingTargetUserMapper.selectOne(targetUserDTO) == null) {
            if (messageSettingTargetUserMapper.insertSelective(targetUserDTO) != 1) {
                throw new CommonException("error.insert.message.setting.target.user");
            }
        }
    }

    public boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    private void saveMessageSettingTargetUser(DevopsNotificationTransferDataVO devopsNotificationTransferDataVO, MessageSettingDTO messageSettingDTO) {
        TargetUserDTO targetUserDTO = new TargetUserDTO();
        targetUserDTO.setMessageSettingId(messageSettingDTO.getId());
        if ("specifier".equals(devopsNotificationTransferDataVO.getNotifyObject())) {
            targetUserDTO.setType(TargetUserType.SPECIFIER.getTypeName());
            devopsNotificationTransferDataVO.getUserRelDTOS().stream().forEach(u -> {
                targetUserDTO.setUserId(u.getUserId());
                targetUserDTO.setId(null);
                targetUserDTO.setType("specifier");
                insertTargetUser(targetUserDTO);
            });
        } else if ("owner".equals(devopsNotificationTransferDataVO.getNotifyObject())) {
            targetUserDTO.setId(null);
            targetUserDTO.setType(TargetUserType.PROJECT_OWNER.getTypeName());
            insertTargetUser(targetUserDTO);
        } else if ("handler".equals(devopsNotificationTransferDataVO.getNotifyObject())) {
            targetUserDTO.setId(null);
            targetUserDTO.setType(TargetUserType.HANDLER.getTypeName());
            insertTargetUser(targetUserDTO);
        }
    }

    private void fillMessageSettings(MessageSettingDTO messageSettingDTO, List<String> notifyType) {
        for (String type : notifyType) {
            messageSettingDTO.setEmailEnable(false);
            messageSettingDTO.setSmsEnable(false);
            messageSettingDTO.setPmEnable(false);
            if ("sms".equals(type)) {
                messageSettingDTO.setSmsEnable(true);
            }
            if ("email".equals(type)) {
                messageSettingDTO.setEmailEnable(true);
            }
            if ("pm".equals(type)) {
                messageSettingDTO.setPmEnable(true);
            }
        }
    }

    private void insertTargetUser(TargetUserDTO targetUserDTO) {
        if (messageSettingTargetUserMapper.selectOne(targetUserDTO) == null) {
            if (messageSettingTargetUserMapper.insertSelective(targetUserDTO) != 1) {
                throw new CommonException("error.insert.message.setting.target.user");
            }
        }
    }

    private List<String> fillName(List<String> recouseNameList, DevopsNotificationTransferDataVO devopsNotificationTransferDataVO) {
        if (devopsNotificationTransferDataVO.getNotifyTriggerEvent().contains(",")) {
            recouseNameList = Stream.of(devopsNotificationTransferDataVO.getNotifyTriggerEvent().split(",")).collect(Collectors.toList());
        } else {
            recouseNameList.add(devopsNotificationTransferDataVO.getNotifyTriggerEvent());
        }
        return recouseNameList;
    }

    private List<String> fillType(List<String> notifyType, DevopsNotificationTransferDataVO devopsNotificationTransferDataVO) {
        if (devopsNotificationTransferDataVO.getNotifyType().contains(",")) {
            notifyType = Stream.of(devopsNotificationTransferDataVO.getNotifyType().split(",")).collect(Collectors.toList());
        } else {
            notifyType.add(devopsNotificationTransferDataVO.getNotifyType());
        }
        return notifyType;
    }
}
