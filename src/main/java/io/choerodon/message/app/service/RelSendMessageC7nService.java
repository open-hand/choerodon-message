package io.choerodon.message.app.service;

import org.hzero.boot.message.entity.AllSender;
import org.hzero.boot.message.entity.MessageSender;
import org.hzero.message.app.service.RelSendMessageService;
import org.hzero.message.domain.entity.Message;

import java.util.List;

/**
 * @author scp
 * @date 2020/5/13
 * @description
 */
public interface RelSendMessageC7nService extends RelSendMessageService {
    /**
     * 批量发送消息
     * @param senderList senderList
     */
    void batchSendMessage(List<MessageSender> senderList);
}
