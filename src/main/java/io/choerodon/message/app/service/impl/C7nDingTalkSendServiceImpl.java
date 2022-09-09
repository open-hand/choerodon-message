package io.choerodon.message.app.service.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.message.config.MessageClientProperties;
import org.hzero.boot.message.entity.DingTalkMsgType;
import org.hzero.boot.message.entity.DingTalkSender;
import org.hzero.boot.message.entity.WeChatSender;
import org.hzero.core.base.BaseConstants;
import org.hzero.dd.service.DingCorpMessageService;
import org.hzero.dd.service.DingFileStorageService;
import org.hzero.message.api.dto.UserMessageDTO;
import org.hzero.message.app.service.MessageGeneratorService;
import org.hzero.message.app.service.impl.DingTalkSendServiceImpl;
import org.hzero.message.config.MessageConfigProperties;
import org.hzero.message.domain.entity.Message;
import org.hzero.message.domain.entity.MessageReceiver;
import org.hzero.message.domain.repository.MessageReceiverRepository;
import org.hzero.message.domain.repository.MessageRepository;
import org.hzero.message.domain.repository.MessageTransactionRepository;
import org.hzero.message.domain.vo.DingTalkServerConfig;
import org.hzero.message.infra.retry.MessageSendRetryer;
import org.hzero.message.infra.supporter.DingTalkServerSupporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import io.choerodon.message.app.aop.MessageGeneratorServiceAop;

/**
 * @author scp
 * @since 2022/5/25 c7n覆盖{@link #resendMessage}
 */
@Service
@Primary
public class C7nDingTalkSendServiceImpl extends DingTalkSendServiceImpl {
    @Autowired
    private MessageGeneratorService messageGeneratorService;
    @Autowired
    private MessageSendRetryer messageSendRetryer;
    @Autowired
    private DingCorpMessageService dingCorpMessageService;
    @Autowired
    private MessageReceiverRepository messageReceiverRepository;
    @Autowired
    private MessageGeneratorServiceAop messageGeneratorServiceAop;


    public C7nDingTalkSendServiceImpl(MessageRepository messageRepository, DingCorpMessageService dingCorpMessageService, MessageConfigProperties messageConfigProperties, MessageGeneratorService messageGeneratorService, MessageReceiverRepository messageReceiverRepository, MessageTransactionRepository messageTransactionRepository, MessageClientProperties messageClientProperties, MessageSendRetryer messageSendRetryer, DingFileStorageService dingFileStorageService) {
        super(messageRepository, dingCorpMessageService, messageConfigProperties, messageGeneratorService, messageReceiverRepository, messageTransactionRepository, messageClientProperties, messageSendRetryer, dingFileStorageService);
    }

    @Override
    public Message sendMessage(DingTalkSender dingTalkSender, Integer tryTimes) {
        Long tenantId = dingTalkSender.getTenantId();
        Boolean enabled = RelSendMessageC7nServiceImpl.isMessageEnabled(tenantId, "ding_talk");
        if (Boolean.TRUE.equals(enabled)) {
            return super.sendMessage(dingTalkSender, tryTimes);
        } else {
            return null;
        }
    }

    @Override
    public Message resendMessage(UserMessageDTO message) {
        if (CollectionUtils.isEmpty(message.getMessageReceiverList())) {
            return message;
        }
        // 发送消息
        try {
            List<String> userList = message.getMessageReceiverList().stream().map(MessageReceiver::getReceiverAddress).collect(Collectors.toList());
            DingTalkMsgType msgType = null;
            Map<String, Object> map = message.getArgs();
            if (map.containsKey(WeChatSender.FIELD_MSG_TYPE)) {
                msgType = DingTalkMsgType.getType(String.valueOf(map.get(WeChatSender.FIELD_MSG_TYPE)));
                map.remove(WeChatSender.FIELD_MSG_TYPE);
            }
            // 重新生成消息内容
            Message messageContent = messageGeneratorService.generateMessage(message.getTenantId(), message.getTemplateCode(), message.getLang(), map);
            if (messageContent != null) {
                message.setPlainContent(messageContent.getPlainContent());
                // todo 唯一覆盖逻辑
                // 确保钉钉消息重试能正常发送
                if (!ObjectUtils.isEmpty(messageContent.getPlainContent())) {
                    message.setPlainContent(messageGeneratorServiceAop.processMessageContent(messageContent.getPlainContent(), message.getTenantId()));
                } else {
                    message.setContent(messageGeneratorServiceAop.processMessageContent(messageContent.getPlainContent(), message.getTenantId()));
                }
                message.setTemplateEditType("RT");
            }

            sendDingTalkMessage(message.getTenantId(), message.getServerCode(), userList, message, msgType, null);
            successProcessUpdate(message);
        } catch (Exception e) {
            failedProcessUpdate(message, e);
        }
        return message;
    }

    private void sendDingTalkMessage(Long tenantId, String serverCode, List<String> userIdList, Message message, DingTalkMsgType msgType, Integer tryTimes) throws ExecutionException, RetryException {
        List<String> receiverList = saveReceiver(tenantId, userIdList, message);
        String token = DingTalkServerConfig.getToken(tenantId, serverCode);
        Assert.isTrue(StringUtils.isNotBlank(token), BaseConstants.ErrorCode.DATA_INVALID);
        Retryer<?> retry = messageSendRetryer.buildRetry(tryTimes);
        retry.call(() -> {
            DingTalkServerSupporter.sendMessage(dingCorpMessageService, token, receiverList, message, msgType);
            return null;
        });
    }


    private List<String> saveReceiver(Long tenantId, List<String> userList, Message message) {
        // 记录接收人
        userList.forEach(receiver -> messageReceiverRepository.insertSelective(new MessageReceiver().setMessageId(message.getMessageId()).setTenantId(tenantId).setReceiverAddress(receiver)));
        return userList;
    }


}
