package io.choerodon.notify.infra.asserts;

import io.choerodon.core.exception.FeignException;
import io.choerodon.notify.domain.SmsConfigDTO;
import io.choerodon.notify.infra.mapper.SmsConfigMapper;
import org.springframework.stereotype.Component;

/**
 * @author superlee
 * @since 2019-05-17
 */
@Component
public class SmsConfigAssertHelper {

    private SmsConfigMapper smsConfigMapper;

    public SmsConfigAssertHelper(SmsConfigMapper smsConfigMapper) {
        this.smsConfigMapper = smsConfigMapper;
    }

    public SmsConfigDTO smsConfigNotExisted(WhichColumn whichColumn, Long value) {
        switch (whichColumn) {
            case ID:
                return notExistedById(value, "error.smsConfig.not.existed");
            case ORGANIZATION_ID:
                return notExistedByOrganizationId(value, "error.smsConfig.not.existed");
            default:
                throw new FeignException("error.illegal.whichColumn", whichColumn.value);
        }
    }

    private SmsConfigDTO notExistedByOrganizationId(Long value, String message) {
        SmsConfigDTO dto = new SmsConfigDTO();
        dto.setOrganizationId(value);
        SmsConfigDTO result = smsConfigMapper.selectOne(dto);
        if (result == null) {
            throw new FeignException(message);
        }
        return result;
    }

    private SmsConfigDTO notExistedById(Long value, String message) {
        SmsConfigDTO dto = smsConfigMapper.selectByPrimaryKey(value);
        if (dto == null) {
            throw new FeignException(message);
        }
        return dto;
    }

    public SmsConfigDTO smsConfigNotExisted(WhichColumn whichColumn, Long value, String message) {
        switch (whichColumn) {
            case ID:
                return notExistedById(value, message);
            case ORGANIZATION_ID:
                return notExistedByOrganizationId(value, message);
            default:
                throw new FeignException("error.illegal.whichColumn", whichColumn.value);
        }
    }

    public enum WhichColumn {

        /**
         * id
         */
        ID("id"),

        /**
         * 组织id
         */
        ORGANIZATION_ID("organization_id");

        private String value;

        WhichColumn(String value) {
            this.value = value;
        }

        public static boolean contains(String value) {
            for (WhichColumn whichColumn : WhichColumn.values()) {
                if (whichColumn.value.equals(value)) {
                    return true;
                }
            }
            return false;
        }
    }
}
