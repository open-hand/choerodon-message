package io.choerodon.message.app.task;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.choerodon.asgard.schedule.annotation.JobParam;
import io.choerodon.asgard.schedule.annotation.JobTask;
import io.choerodon.message.infra.mapper.MessageC7nMapper;

/**
 * Created by wangxiang on 2020/8/25
 */
@Component
public class HzeroMessageTask {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    /**
     * 清理消息类型 WEBHOOK/EMAIL
     */
    private final String MESSAGE_TYPE = "messageType";

    /**
     * 清理多少天前的数据
     */
    private final String CLEAN_NUM = "cleanNum";

    private MessageC7nMapper messageC7nMapper;

    public HzeroMessageTask(MessageC7nMapper messageC7nMapper) {
        this.messageC7nMapper = messageC7nMapper;
    }


    /**
     * 清除消息发送记录
     *
     * @param data
     * @return map
     */
    @JobTask(code = "cleanMessageRecord", maxRetryCount = 0, description = "清除消息发送记录",
            params = {
                    @JobParam(name = MESSAGE_TYPE, description = "清理消息类型"),
                    @JobParam(name = CLEAN_NUM, description = "清理多少天前的数据")})
    public Map<String, Object> cleanRecord(Map<String, Object> data) {
        LOGGER.info(">>>>>>>>>>>>>>>>>>>>begin clearing records<<<<<<<<<<<<<<<<<<<<<<<<<<");
        try {
            messageC7nMapper.deleteRecord(String.valueOf(data.get(MESSAGE_TYPE)), Integer.valueOf(String.valueOf(data.get(CLEAN_NUM))));
        } catch (Exception e) {
            LOGGER.error("error.clean.records", e);
        }
        LOGGER.info(">>>>>>>>>>>>>>>>>>>>end clearing records<<<<<<<<<<<<<<<<<<<<<<<<<<");
        return data;
    }

}
