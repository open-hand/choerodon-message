package io.choerodon.message.app.service;

import java.util.Map;

import org.hzero.message.api.dto.SimpleMessageDTO;

import io.choerodon.mybatis.pagehelper.domain.PageRequest;

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

    /**
     * 计算当前用户未读消息数
     * @param pageRequest
     * @return
     */
    Map<String, Integer> countUnreadMessageMap(PageRequest pageRequest);

}
