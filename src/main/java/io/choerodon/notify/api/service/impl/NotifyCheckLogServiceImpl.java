package io.choerodon.notify.api.service.impl;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.alibaba.fastjson.JSON;
import com.zaxxer.hikari.util.UtilityElf;
import io.choerodon.core.notify.ServiceNotifyType;
import io.choerodon.core.notify.TargetUserType;
import io.choerodon.notify.api.dto.DevopsNotificationUserRelVO;
import io.choerodon.notify.api.dto.DevopsNotificationVO;
import io.choerodon.notify.infra.dto.MessageSettingDTO;
import io.choerodon.notify.infra.dto.TargetUserDTO;
import io.choerodon.notify.infra.enums.DeleteResourceType;
import io.choerodon.notify.infra.feign.DevopsFeignClient;
import io.choerodon.notify.infra.mapper.MessageSettingMapper;
import io.choerodon.notify.infra.mapper.MessageSettingTargetUserMapper;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.core.exception.CommonException;
import io.choerodon.notify.api.dto.CheckLog;
import io.choerodon.notify.api.dto.DevopsNotificationVO;
import io.choerodon.notify.api.dto.MessageDetailDTO;
import io.choerodon.notify.api.service.NotifyCheckLogService;
import io.choerodon.notify.infra.dto.MessageSettingDTO;
import io.choerodon.notify.infra.dto.NotifyCheckLogDTO;
import io.choerodon.notify.infra.dto.SendSettingDTO;
import io.choerodon.notify.infra.dto.TargetUserDTO;
import io.choerodon.notify.infra.feign.AgileFeignClient;
import io.choerodon.notify.infra.feign.DevopsFeignClient;
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
    private static final Map<String, String> targetUserType = new HashMap<>(4);

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
    private DevopsFeignClient devopsFeignClient;

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
        private  void transferDevopsData(){
            List<DevopsNotificationVO> devopsNotificationVOS = devopsFeignClient.transferData(1L).getBody();
            if (devopsNotificationVOS == null || devopsNotificationVOS.size() == 0) {
                LOGGER.info("No data to migrate");
            }
            MessageSettingDTO messageSettingDTO = new MessageSettingDTO();
            devopsNotificationVOS.stream().forEach(e -> {
                messageSettingDTO.setCode("resourceDeleteConfirmation");
                messageSettingDTO.setProjectId(Objects.isNull(e.getProjectId()) ? null : e.getProjectId());
                messageSettingDTO.setNotifyType("resourceDelete");
                messageSettingDTO.setEmailEnable(Objects.isNull(e.getSendEmail()) ? false : e.getSendEmail());
                messageSettingDTO.setSmsEnable(Objects.isNull(e.getSendSms()) ? false : e.getSendSms());
                messageSettingDTO.setPmEnable(Objects.isNull(e.getSendPm()) ? false : e.getSendPm());
                messageSettingDTO.setEnvId(Objects.isNull(e.getEnvId()) ? null : e.getEnvId());
                List<String> stringList = new ArrayList<>();
                if (e.getNotifyTriggerEvent().contains(",")) {
                    stringList = Stream.of(e.getNotifyTriggerEvent().split(",")).collect(Collectors.toList());
                } else {
                    stringList.add(e.getNotifyTriggerEvent());
                }
                stringList.stream().forEach(v -> {
                    messageSettingDTO.setEventName(v);
                    messageSettingMapper.insert(messageSettingDTO);
                });
                //插入通知对象
                MessageSettingDTO condition = new MessageSettingDTO();
                stringList.stream().forEach(k -> {
                    condition.setEventName(k);
                    condition.setEnvId(messageSettingDTO.getEnvId());
                    MessageSettingDTO messageSettingDTO1 = messageSettingMapper.selectOne(condition);
                    TargetUserDTO targetUserDTO = new TargetUserDTO();

                    targetUserDTO.setMessageSettingId(messageSettingDTO1.getId());
                    List<DevopsNotificationUserRelVO> userList = e.getUserList();
                    userList.stream().forEach(u -> {
                        if ("specifier".equals(u.getUserType())) {
                            targetUserDTO.setUserId(u.getUserId());
                            targetUserDTO.setType(TargetUserType.SPECIFIER.getTypeName());
                            messageSettingTargetUserMapper.insert(targetUserDTO);
                        } else if ("owner".equals(u.getUserType())) {
                            targetUserDTO.setUserId(u.getUserId());
                            targetUserDTO.setType(TargetUserType.PROJECT_OWNER.getTypeName());
                            messageSettingTargetUserMapper.insert(targetUserDTO);
                        } else if ("handler".equals(u.getUserType())) {
                            targetUserDTO.setUserId(u.getUserId());
                            targetUserDTO.setType(TargetUserType.CREATOR.getTypeName());
                            messageSettingTargetUserMapper.insert(targetUserDTO);
                        }
                    });
                });
            });

        }

        @Override
        public void run() {
            try {
                NotifyCheckLogDTO notifyCheckLogDTO = new NotifyCheckLogDTO();
                List<CheckLog> logs = new ArrayList<>();
                notifyCheckLogDTO.setBeginCheckDate(new Date());
                if ("0.20.0".equals(version) && type.equals("devops")) {
                    // todo
                    LOGGER.info("Migration data start");
                    transferDevopsData();
                }
                if ("0.20.0".equals(version) && type.equals("agile")) {
                    syncAgileNotify(logs);
                } else {
                    LOGGER.info("version not matched");
                }

                notifyCheckLogDTO.setLog(JSON.toJSONString(logs));
                notifyCheckLogDTO.setEndCheckDate(new Date());
                notifyCheckLogMapper.insert(notifyCheckLogDTO);
            } catch (Throwable ex) {
                LOGGER.warn("Exception occurred when applying data migration. The ex is: {}", ex);
            }
        }
    }

    void syncAgileNotify(List<CheckLog> logs) {
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
                    map.getValue().forEach(v -> {
                        MessageSettingDTO messageSettingDTO = new MessageSettingDTO();
                        messageSettingDTO.setProjectId(map.getKey());
                        messageSettingDTO.setNotifyType(AGILE);
                        SendSettingDTO sendSettingDTO = selectSendSettingByCode(codeMap.get(v.getEvent()));
                        if (sendSettingDTO.getPmEnabledFlag()) {
                            messageSettingDTO.setPmEnable(true);
                        } else {
                            messageSettingDTO.setPmEnable(false);
                        }
                        messageSettingDTO.setCode(codeMap.get(v.getEvent()));
                        if (messageSettingMapper.insertSelective(messageSettingDTO) != 1) {
                            throw new CommonException("error.insert.message.send.setting");
                        }

                        if (v.getNoticeType().equals("users")) {
                            String[] userIds = v.getUser().split(",");
                            for (String userId : userIds) {
                                TargetUserDTO targetUserDTO = new TargetUserDTO();
                                targetUserDTO.setMessageSettingId(messageSettingDTO.getId());
                                targetUserDTO.setType(targetUserType.get(v.getNoticeType()));
                                targetUserDTO.setUserId(Long.valueOf(userId));
                                messageSettingTargetUserMapper.insertSelective(targetUserDTO);
                            }
                        } else {
                            TargetUserDTO targetUserDTO = new TargetUserDTO();
                            targetUserDTO.setMessageSettingId(messageSettingDTO.getId());
                            targetUserDTO.setType(targetUserType.get(v.getNoticeType()));
                            messageSettingTargetUserMapper.insertSelective(targetUserDTO);
                        }
                    });
                    checkLog.setResult("Succeed to sync agile notify!");
                } catch (Exception e) {
                    checkLog.setResult("Failed to sync agile notify!");
                }
                logs.add(checkLog);
            }
        }

    }

    private SendSettingDTO selectSendSettingByCode(String code) {
        SendSettingDTO sendSettingDTO = new SendSettingDTO();
        sendSettingDTO.setCode(code);
        sendSettingDTO = sendSettingMapper.selectOne(sendSettingDTO);
        if (sendSettingDTO == null) {
            throw new CommonException(SendSettingServiceImpl.SEND_SETTING_DOES_NOT_EXIST);
        }
        return sendSettingDTO;
    }

}
