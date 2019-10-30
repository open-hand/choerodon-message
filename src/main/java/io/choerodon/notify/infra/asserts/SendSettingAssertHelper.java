package io.choerodon.notify.infra.asserts;

import io.choerodon.core.exception.FeignException;
import io.choerodon.notify.infra.dto.SendSettingDTO;
import io.choerodon.notify.infra.mapper.SendSettingMapper;
import org.springframework.stereotype.Component;

/**
 * @author superlee
 * @since 2019-05-17
 */
@Component
public class SendSettingAssertHelper {

    private SendSettingMapper sendSettingMapper;

    public SendSettingAssertHelper(SendSettingMapper sendSettingMapper) {
        this.sendSettingMapper = sendSettingMapper;
    }

    public SendSettingDTO sendSettingNotExisted(String code) {
        return sendSettingNotExisted(code, "error.sms.sendSetting.not.exist");
    }

    public SendSettingDTO sendSettingNotExisted(String code, String message) {
        SendSettingDTO sendSetting = new SendSettingDTO();
        sendSetting.setCode(code);
        SendSettingDTO result = sendSettingMapper.selectOne(sendSetting);
        if (result == null) {
            throw new FeignException(message);
        }
        return result;

    }

}
