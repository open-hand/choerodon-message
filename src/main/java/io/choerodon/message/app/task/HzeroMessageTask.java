package io.choerodon.message.app.task;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.choerodon.asgard.schedule.annotation.JobTask;
import io.choerodon.message.infra.mapper.MessageC7nMapper;

/**
 * Created by wangxiang on 2020/8/25
 */
@Component
public class HzeroMessageTask {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private MessageC7nMapper messageC7nMapper;

    public HzeroMessageTask(MessageC7nMapper messageC7nMapper) {
        this.messageC7nMapper = messageC7nMapper;
    }


    /**
     * 需自动清除掉半年前的发送记录(webhook)
     *
     * @param data
     * @return map
     */
    @JobTask(code = "cleanWebhookRecord", maxRetryCount = 2, description = "清除掉半年前的发送记录(webhook)")
    public Map<String, Object> cleanWebhookRecord(Map<String, Object> data) {
        LOGGER.info(">>>>>>>>>>>>>>>>>>>>begin clearing webhook records<<<<<<<<<<<<<<<<<<<<<<<<<<");
        try {
            messageC7nMapper.deleteRecordHalfAYear();
        } catch (Exception e) {
            LOGGER.error("error.clean.webhook.records", e);
        }
        LOGGER.info(">>>>>>>>>>>>>>>>>>>>end clearing webhook records<<<<<<<<<<<<<<<<<<<<<<<<<<");
        return data;
    }

}
