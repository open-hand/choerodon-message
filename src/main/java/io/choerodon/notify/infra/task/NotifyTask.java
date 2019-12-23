package io.choerodon.notify.infra.task;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.asgard.schedule.QuartzDefinition;
import io.choerodon.asgard.schedule.annotation.JobTask;
import io.choerodon.asgard.schedule.annotation.TimedTask;
import io.choerodon.notify.api.service.NotifyCheckLogService;

/**
 * @author scp
 */
@Component
public class NotifyTask {
    private static final Logger logger = LoggerFactory.getLogger(NotifyTask.class);

    @Autowired
    private NotifyCheckLogService notifyCheckLogService;

    /**
     * 升级0.19.0-0.20.0，迁移数据
     */
    @JobTask(maxRetryCount = 3, code = "upgradeVersionTo20Notify", params = {}, description = "升级notify-service,0.19.0-0.20.0，迁移数据")
    @TimedTask(name = "upgradeVersionTo20Notify", description = "升级notify-service,0.19.0-0.20.0，迁移数据", oneExecution = true,
            repeatCount = 0, repeatInterval = 1, repeatIntervalUnit = QuartzDefinition.SimpleRepeatIntervalUnit.HOURS, params = {})
    public void syncNotifyData(Map<String, Object> map) {
        logger.info("begin to upgrade 0.19.0 to 0.20.0 notify send setting.");
        notifyCheckLogService.checkLog("0.20.0","notify");
    }

}
