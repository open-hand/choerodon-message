package io.choerodon.notify.api.service.impl;

import com.alibaba.fastjson.JSON;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


import com.zaxxer.hikari.util.UtilityElf;
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
}
