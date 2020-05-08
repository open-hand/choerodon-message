package io.choerodon.message.infra.validator;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.message.infra.enums.SendingTypeEnum;

/**
 * @author dengyouquan
 **/
public class CommonValidator {
    private CommonValidator() {
    }

    /**
     * 校验层级是否在ResourceLevel中(不包括user)
     *
     * @param sourceType
     */
    public static void validatorLevel(final String sourceType) {
        for (ResourceLevel level : ResourceLevel.values()) {
            if (level.value().equalsIgnoreCase(sourceType) && !sourceType.equalsIgnoreCase(ResourceLevel.USER.value())) {
                return;
            }
        }
        throw new CommonException("error.notify.sourceType");
    }

    /**
     * 校验消息类型是否在MessageType中
     *
     * @param messageType
     */
    public static void validatorMessageType(final String messageType) {
        for (SendingTypeEnum type : SendingTypeEnum.values()) {
            if (type.getValue().equalsIgnoreCase(messageType)) {
                return;
            }
        }
        throw new CommonException("error.notify.messageType");
    }
}
