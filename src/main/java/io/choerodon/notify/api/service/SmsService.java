package io.choerodon.notify.api.service;

import io.choerodon.notify.api.dto.NoticeSendDTO;
import io.choerodon.notify.infra.dto.SmsConfigDTO;

/**
 * @author superlee
 * @since 2019-07-24
 */
public interface SmsService {
    /**
     * 发送短信
     * @param dto
     */
    void send(NoticeSendDTO dto);

    /**
     * 根据组织id查询短信配置
     *
     * @param organizationId
     * @return
     */
    SmsConfigDTO queryConfig(Long organizationId);

    /**
     * 根据组织id更新配置，如果没有则新建
     * @param id
     * @param smsConfigDTO
     * @return
     */
    SmsConfigDTO updateConfig(Long id, SmsConfigDTO smsConfigDTO);
}
