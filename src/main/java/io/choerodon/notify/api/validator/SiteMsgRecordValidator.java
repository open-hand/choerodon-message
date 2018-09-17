package io.choerodon.notify.api.validator;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;

/**
 * @author dengyouquan
 **/
public class SiteMsgRecordValidator {

    private SiteMsgRecordValidator() {
    }

    public static void validateCurrentUser(Long userId) {
        if (!DetailsHelper.getUserDetails().getUserId().equals(userId)) {
            throw new CommonException("error.siteMsgRecord.notCurrentUser");
        }
    }
}
