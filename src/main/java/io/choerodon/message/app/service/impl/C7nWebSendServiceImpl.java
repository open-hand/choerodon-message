package io.choerodon.message.app.service.impl;

import org.hzero.boot.message.MessageClient;
import org.hzero.boot.message.entity.MessageSender;
import org.hzero.message.app.service.MessageGeneratorService;
import org.hzero.message.app.service.MessageReceiverService;
import org.hzero.message.app.service.UserMessageService;
import org.hzero.message.app.service.impl.WebSendServiceImpl;
import org.hzero.message.domain.entity.Message;
import org.hzero.message.domain.repository.MessageReceiverRepository;
import org.hzero.message.domain.repository.MessageRepository;
import org.hzero.message.domain.repository.MessageTransactionRepository;
import org.hzero.message.domain.repository.UserMessageRepository;
import org.hzero.message.domain.service.IMessageLangService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import io.choerodon.message.infra.utils.JsonHelper;

/**
 * Created by wangxiang on 2021/5/18
 */
@Service
@Primary
public class C7nWebSendServiceImpl extends WebSendServiceImpl {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private MessageClient messageClient;

    public C7nWebSendServiceImpl(MessageGeneratorService messageGeneratorService, UserMessageService userMessageService, MessageRepository messageRepository, MessageReceiverRepository messageReceiverRepository, MessageTransactionRepository messageTransactionRepository, UserMessageRepository userMessageRepository, MessageReceiverService messageReceiverService, IMessageLangService messageLangService) {
        super(messageGeneratorService, userMessageService, messageRepository, messageReceiverRepository, messageTransactionRepository, userMessageRepository, messageReceiverService, messageLangService);
    }


    @Override
    public Message sendMessage(Long organizationId, MessageSender messageSender) {
        Message message = super.sendMessage(organizationId, messageSender);
        String marshalByJackson = JsonHelper.marshalByJackson(message);
        LOGGER.info(">>marshalByJackson:{}>>", marshalByJackson);
        //推送给前端
        // TODO: 2021/5/18  
        return message;
    }
}
