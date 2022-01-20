package io.choerodon.message.app.service.impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.message.app.service.UserMessageService;
import org.hzero.message.domain.entity.Message;
import org.hzero.message.domain.entity.UserMessage;
import org.hzero.message.domain.repository.MessageReceiverRepository;
import org.hzero.message.domain.repository.MessageRepository;
import org.hzero.message.domain.repository.MessageTransactionRepository;
import org.hzero.message.domain.repository.UserMessageRepository;
import org.hzero.message.infra.constant.HmsgConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import io.choerodon.message.app.service.CleanService;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author scp 覆盖hzero清楚消息记录方法
 * @since 2022/01/20
 */
@Service
public class CleanServiceImpl implements CleanService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CleanServiceImpl.class);

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserMessageRepository userMessageRepository;
    @Autowired
    private UserMessageService userMessageService;
    @Autowired
    private MessageTransactionRepository messageTransactionRepository;
    @Autowired
    private MessageReceiverRepository messageReceiverRepository;

    @Override
    public void clearLog(Long tenantId, String cleanStrategy) {
        LocalDate now = LocalDate.now();
        switch (cleanStrategy) {
            case HmsgConstant.DataCleanStrategy.ONE_DAY:
                asyncClearLog(now.minus(1, ChronoUnit.DAYS), tenantId);
                break;
            case HmsgConstant.DataCleanStrategy.THREE_DAY:
                asyncClearLog(now.minus(3, ChronoUnit.DAYS), tenantId);
                break;
            case HmsgConstant.DataCleanStrategy.ONE_WEEK:
                asyncClearLog(now.minus(1, ChronoUnit.WEEKS), tenantId);
                break;
            case HmsgConstant.DataCleanStrategy.ONE_MONTH:
                asyncClearLog(now.minus(1, ChronoUnit.MONTHS), tenantId);
                break;
            case HmsgConstant.DataCleanStrategy.THREE_MONTH:
                asyncClearLog(now.minus(3, ChronoUnit.MONTHS), tenantId);
                break;
            case HmsgConstant.DataCleanStrategy.SIX_MONTH:
                asyncClearLog(now.minus(6, ChronoUnit.MONTHS), tenantId);
                break;
            case HmsgConstant.DataCleanStrategy.ONE_YEAR:
                asyncClearLog(now.minus(1, ChronoUnit.YEARS), tenantId);
                break;
            case HmsgConstant.DataCleanStrategy.ALL:
                asyncClearLog(null, tenantId);
                break;
            default:
                break;
        }
    }

    @Async("commonAsyncTaskExecutor")
    @Override
    public void asyncClearLog(LocalDate localDate, Long tenantId) {
        PageRequest pageRequest = new PageRequest(0, 20);
        List<Message> messageList;
        while (true) {
            LOGGER.info("===========delete message record1=====");
            messageList = messageRepository.listMessage(tenantId, localDate, pageRequest);
            if (CollectionUtils.isEmpty(messageList)) {
                return;
            }
            // 覆盖hzero逻辑
            try {
                messageList.forEach(message -> {
                    Long messageId = message.getMessageId();
                    // 删除站内消息
                    if (Objects.equals(message.getMessageTypeCode(), HmsgConstant.MessageType.WEB)) {
                        List<UserMessage> userMessageList = userMessageRepository.select(new UserMessage().setMessageId(messageId));
                        userMessageList.forEach(userMessage -> {
                            if (Objects.equals(userMessage.getReadFlag(), BaseConstants.Flag.NO)) {
                                List<Long> idList = new ArrayList<>();
                                idList.add(messageId);
                                userMessageService.readMessage(userMessage.getTenantId(), userMessage.getUserId(), idList);
                            }
                        });
                        userMessageRepository.delete(new UserMessage().setMessageId(messageId));
                    }
                    LOGGER.info("===========delete message record id:{}=====", messageId);
                });
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
            List<Long> messageIds = messageList.stream().map(Message::getMessageId).collect(Collectors.toList());
            // 批量删除事务
            messageRepository.batchDeleteTransactionByIds(messageIds);
            // 批量删除接收人
            messageRepository.batchDeleteReceiverByIds(messageIds);
            // 批量删除消息
            messageRepository.batchDeleteByIds(messageIds);
            LOGGER.info("===========delete message record2=====");
        }
    }
}
