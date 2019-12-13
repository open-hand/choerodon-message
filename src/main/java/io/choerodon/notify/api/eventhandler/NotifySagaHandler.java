package io.choerodon.notify.api.eventhandler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.core.notify.NotifyType;
import io.choerodon.core.notify.ServiceNotifyType;
import io.choerodon.notify.api.eventhandler.constants.SagaTaskCodeConstants;
import io.choerodon.notify.api.eventhandler.constants.SagaTopicCodeConstants;
import io.choerodon.notify.api.service.MessageSettingService;

@Component
public class NotifySagaHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotifySagaHandler.class);
    @Autowired
    private MessageSettingService messageSettingService;

    /**
     * devops删除环境
     */
    @SagaTask(code = SagaTaskCodeConstants.MESSAGE_DELETE_ENV,
            description = "notify删除环境对应的资源删除通知设置",
            sagaCode = SagaTopicCodeConstants.DEVOPS_DELETE_ENV,
            maxRetryCount = 3,
            seq = 1)
    public void deleteByTypeAndEnvId(String data) {
        JSONObject jsonObject = JSON.parseObject(data);
        Long envId = Long.valueOf(jsonObject.get("envId").toString());
        messageSettingService.deleteByTypeAndEnvId(ServiceNotifyType.RESOURCE_DELETE_NOTIFY.getTypeName(), envId);
        LOGGER.info(data);
    }
}
