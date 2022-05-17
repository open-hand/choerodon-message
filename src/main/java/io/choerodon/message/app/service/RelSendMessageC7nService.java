package io.choerodon.message.app.service;

import java.util.List;
import java.util.Map;

import org.hzero.boot.message.entity.MessageSender;
import org.hzero.message.app.service.RelSendMessageService;
import org.hzero.message.domain.entity.Message;
import org.hzero.message.domain.entity.TemplateServerLine;

/**
 * @author scp
 * @date 2020/5/13
 * @description
 */
public interface RelSendMessageC7nService extends RelSendMessageService {
    /**
     * 批量发送消息
     *
     * @param senderList senderList
     */
    void batchSendMessage(List<MessageSender> senderList);

    List<Message> c7nRelSendMessageReceipt(MessageSender messageSender, Long organizationId);

    void sendDingTalk(Map<String, List<TemplateServerLine>> serverLineMap, List<Message> result, MessageSender sender, String sendDingTalk);

    void filterDingTalkReceiver(MessageSender sender);
}
