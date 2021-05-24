package io.choerodon.message.app.service;

import org.hzero.message.api.dto.SimpleMessageDTO;

/**
 * @author zmf
 * @since 2020/6/3
 */
public interface C7nMessageService {
    /**
     * 彻底删除用户当前的所有站内信
     */
    void deleteAllSiteMessages();

    SimpleMessageDTO getSimpleMessageDTO(Long messageId);

}
