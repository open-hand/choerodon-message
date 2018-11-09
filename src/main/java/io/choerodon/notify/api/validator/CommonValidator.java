package io.choerodon.notify.api.validator;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;

/**
 * @author dengyouquan
 **/
public class CommonValidator {
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
        throw new CommonException("error.receiveSetting.sourceType");
    }
}
