package io.choerodon.message.app.eventhandler;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.asgard.saga.SagaDefinition;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.message.app.eventhandler.payload.UserMemberEventPayload;
import io.choerodon.message.app.service.MessageSettingC7nService;
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
            seq = 2)
    public void asyncMessageProjectUser(String paylod) {
        try {
            JavaType javaType = getCollectionType(ArrayList.class, UserMemberEventPayload.class);
            List<UserMemberEventPayload> userMemberEventPayloads = objectMapper.readValue(paylod, javaType);
            messageSettingC7nService.asyncMessageProjectUser(userMemberEventPayloads);
        } catch (Exception e) {
            logger.error("async.project.message.setting.user.error", e);
        }
    }

    public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }
}
