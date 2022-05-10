package io.choerodon.message.app.eventhandler;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.asgard.saga.SagaDefinition;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.message.api.vo.OpenAppVO;
import io.choerodon.message.app.eventhandler.payload.UserMemberEventPayload;
import io.choerodon.message.app.service.MessageSettingC7nService;
import io.choerodon.message.infra.utils.JsonHelper;
import io.choerodon.message.infra.utils.SagaTopic;

/**
 * Created by wangxiang on 2020/10/13
 */
@Component
public class MessageSagaHandler {

    private static Logger logger = LoggerFactory.getLogger(MessageSagaHandler.class);
    private static ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MessageSettingC7nService messageSettingC7nService;


    @SagaTask(code = SagaTopic.MESSAGE_DELETE_PROJECT_USER,
            description = "删除项目角色同步消息通知对象",
            sagaCode = SagaTopic.MEMBER_ROLE_DELETE,
            maxRetryCount = 3,
            concurrentLimitPolicy = SagaDefinition.ConcurrentLimitPolicy.NONE,
            seq = 1)
    public void asyncMessageProjectUser(String paylod) {
        try {
            JavaType javaType = getCollectionType(ArrayList.class, UserMemberEventPayload.class);
            List<UserMemberEventPayload> userMemberEventPayloads = objectMapper.readValue(paylod, javaType);
            messageSettingC7nService.asyncMessageProjectUser(userMemberEventPayloads);
        } catch (Exception e) {
            logger.error("async.project.message.setting.user.error", e);
        }
    }

    @SagaTask(code = SagaTopic.INSERT_OPEN_APP_SYNC_SETTING,
            description = "同步开放应用配置到消息服务",
            sagaCode = SagaTopic.INSERT_OPEN_APP_SYNC_SETTING,
            maxRetryCount = 3,
            concurrentLimitPolicy = SagaDefinition.ConcurrentLimitPolicy.NONE,
            seq = 1)
    public void insertOpenAppSyncSetting(String paylod) {
        try {
            OpenAppVO openAppVO = JsonHelper.unmarshalByJackson(paylod, OpenAppVO.class);
            messageSettingC7nService.insertOpenAppConfig(openAppVO);
        } catch (Exception e) {
            logger.error("async.project.message.setting.user.error", e);
        }
    }

    @SagaTask(code = SagaTopic.UPDATE_OPEN_APP_SYNC_SETTING,
            description = "更新开放应用配置到消息服务",
            sagaCode = SagaTopic.UPDATE_OPEN_APP_SYNC_SETTING,
            maxRetryCount = 3,
            concurrentLimitPolicy = SagaDefinition.ConcurrentLimitPolicy.NONE,
            seq = 1)
    public void updateOpenAppSyncSetting(String paylod) {
        try {
            OpenAppVO openAppVO = JsonHelper.unmarshalByJackson(paylod, OpenAppVO.class);
            messageSettingC7nService.updateOpenAppConfig(openAppVO);
        } catch (Exception e) {
            logger.error("async.project.message.setting.user.error", e);
        }
    }

    @SagaTask(code = SagaTopic.ENABLE_OR_DISABLE_OPEN_APP_SYNC_SETTING,
            description = "启用或停用开放应用配置",
            sagaCode = SagaTopic.ENABLE_OR_DISABLE_OPEN_APP_SYNC_SETTING,
            maxRetryCount = 3,
            concurrentLimitPolicy = SagaDefinition.ConcurrentLimitPolicy.NONE,
            seq = 1)
    public void enableOrDisableOpenAppSyncSetting(String paylod) {
        try {
            OpenAppVO openAppVO = JsonHelper.unmarshalByJackson(paylod, OpenAppVO.class);
            messageSettingC7nService.enableOrDisableOpenAppSyncSetting(openAppVO);
        } catch (Exception e) {
            logger.error("async.project.message.setting.user.error", e);
        }
    }


    public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }
}
