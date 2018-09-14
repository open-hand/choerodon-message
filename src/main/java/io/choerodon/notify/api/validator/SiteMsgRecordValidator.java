package io.choerodon.notify.api.validator;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.notify.domain.SiteMsgRecord;
import io.choerodon.notify.infra.mapper.SiteMsgRecordMapper;
import org.springframework.stereotype.Component;

/**
 * @author dengyouquan
 **/
public class SiteMsgRecordValidator {

    public static void validateCurrentUser(Long userId) {
        if (!DetailsHelper.getUserDetails().getUserId().equals(userId)) {
            throw new CommonException("error.siteMsgRecord.notCurrentUser");
        }
    }
}
