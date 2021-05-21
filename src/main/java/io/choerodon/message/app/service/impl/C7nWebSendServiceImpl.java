package io.choerodon.message.app.service.impl;

import java.util.List;
import org.hzero.boot.message.MessageClient;
import org.hzero.boot.message.entity.MessageSender;
import org.hzero.boot.message.entity.Receiver;
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
import org.hzero.websocket.helper.SocketSendHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import io.choerodon.message.api.vo.PopMessageVO;
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
    @Autowired
    private SocketSendHelper socketSendHelper;

    public C7nWebSendServiceImpl(MessageGeneratorService messageGeneratorService, UserMessageService userMessageService, MessageRepository messageRepository, MessageReceiverRepository messageReceiverRepository, MessageTransactionRepository messageTransactionRepository, UserMessageRepository userMessageRepository, MessageReceiverService messageReceiverService, IMessageLangService messageLangService) {
        super(messageGeneratorService, userMessageService, messageRepository, messageReceiverRepository, messageTransactionRepository, userMessageRepository, messageReceiverService, messageLangService);
    }


    @Override
    public Message sendMessage(Long organizationId, MessageSender messageSender) {
        Message message = super.sendMessage(organizationId, messageSender);
        String marshalByJackson = JsonHelper.marshalByJackson(message);
        LOGGER.info(">>>>>>>>>>>>>marshalByJackson:{}>>", marshalByJackson);
        //推送给前端
        /**
         * {
         * 	"objectVersionNumber": 3,
         * 	"_token": "HuFIjmaG4cz+TpuCS5HbgArigzGiq+XyrilqFCmujSs3G2VY1/eO0IX8Et2UIAxgOiUxFDmdWD6PviC57nxvmNeCOUuFWjf4X3X/Z8v1uqsy/0KBqoGc9k47eOFR8J0R",
         * 	"messageId": 182536898873528320,
         * 	"tenantId": 0,
         * 	"messageTypeCode": "WEB",
         * 	"templateCode": "DISABLE_ORGANIZATION.WEB",
         * 	"lang": "zh_CN",
         * 	"subject": "组织停用",
         * 	"content": "<p>您好，</p><p>您所在的组织：nyintel 已被停用。</p>",
         * 	"sendFlag": 1,
         * 	"transactionId": 182536915520720896,
         * 	"plainContent": "<p>您好，</p><p>您所在的组织：nyintel 已被停用。</p>",
         * 	"templateEditType": "RT"
         * }
         */

        //获得消息的接收者
        List<Receiver> receiverAddressList = messageSender.getReceiverAddressList();
        PopMessageVO popMessageVO = new PopMessageVO();
        popMessageVO.setContent(message.getContent());
        popMessageVO.setTitle(message.getSubject());
        //推送给前端
        receiverAddressList.forEach(receiver -> {
            socketSendHelper.sendByUserId(receiver.getUserId(), "choerodon-pop-ups", JsonHelper.marshalByJackson(popMessageVO));
        });
        return message;
    }
}
