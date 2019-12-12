package io.choerodon.notify.api.service.impl;

import com.alibaba.fastjson.JSON;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;


import com.zaxxer.hikari.util.UtilityElf;
import io.choerodon.core.notify.TargetUserType;
import io.choerodon.notify.api.dto.DevopsNotificationVO;
import io.choerodon.notify.infra.dto.MessageSettingDTO;
import io.choerodon.notify.infra.enums.DeleteResourceType;
import io.choerodon.notify.infra.feign.DevopsFeignClient;
import io.choerodon.notify.infra.mapper.MessageSettingMapper;
import io.choerodon.notify.infra.mapper.MessageSettingTargetUserMapper;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.notify.api.dto.CheckLog;
import io.choerodon.notify.api.service.NotifyCheckLogService;
import io.choerodon.notify.infra.dto.NotifyCheckLogDTO;
import io.choerodon.notify.infra.mapper.NotifyCheckLogMapper;

@Service
public class NotifyCheckLogServiceImpl implements NotifyCheckLogService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotifyCheckLogServiceImpl.class);
    private static final ExecutorService executorService = new ThreadPoolExecutor(0, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), new UtilityElf.DefaultThreadFactory("notify-upgrade", false));

    @Autowired
    private NotifyCheckLogMapper notifyCheckLogMapper;

    @Autowired
    private DevopsFeignClient devopsFeignClient;

    @Autowired
    private MessageSettingMapper messageSettingMapper;

    @Autowired
    private MessageSettingTargetUserMapper messageSettingTargetUserMapper;

    @Override
    public void checkLog(String version) {
        LOGGER.info("start upgrade task");
        executorService.execute(new UpgradeTask(version));
    }

    class UpgradeTask implements Runnable {
        private String version;

        UpgradeTask(String version) {
            this.version = version;
        }

        @Override
        public void run() {
            try {
                NotifyCheckLogDTO notifyCheckLogDTO = new NotifyCheckLogDTO();
                List<CheckLog> logs = new ArrayList<>();
                notifyCheckLogDTO.setBeginCheckDate(new Date());
                if ("0.20.0".equals(version)) {
                    // todo
                    LOGGER.info("Migration data start");
                    List<DevopsNotificationVO> devopsNotificationVOS = devopsFeignClient.transferData(1L).getBody();
                    if (devopsNotificationVOS == null || devopsNotificationVOS.size() == 0) {
                        LOGGER.info("No data to migrate");
                    }
                    MessageSettingDTO messageSettingDTO = new MessageSettingDTO();
                    devopsNotificationVOS.stream().forEach(e -> {
                        messageSettingDTO.setCode("resourceDeleteConfirmation");
                        messageSettingDTO.setProjectId(Objects.isNull(e.getProjectId()) ? null : e.getProjectId());

                        messageSettingDTO.setCategory("环境的名字");
                        messageSettingDTO.setNotifyType("DeleteResource");

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
                    });

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

    //todo scp
    void syncAgileNotify() {

    }
}
