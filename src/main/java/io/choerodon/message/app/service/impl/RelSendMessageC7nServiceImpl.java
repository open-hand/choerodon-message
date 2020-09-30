package io.choerodon.message.app.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.hzero.boot.message.entity.MessageSender;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.boot.message.entity.WebHookSender;
import org.hzero.message.app.service.TemplateServerService;
import org.hzero.message.app.service.impl.RelSendMessageServiceImpl;
import org.hzero.message.domain.entity.TemplateServer;
import org.hzero.message.infra.constant.HmsgConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import io.choerodon.core.enums.MessageAdditionalType;
import io.choerodon.message.app.service.RelSendMessageC7nService;
import io.choerodon.message.infra.dto.MessageSettingDTO;
import io.choerodon.message.infra.dto.WebhookProjectRelDTO;
import io.choerodon.message.infra.mapper.MessageSettingC7nMapper;
import io.choerodon.message.infra.mapper.ReceiveSettingC7nMapper;
import io.choerodon.message.infra.mapper.WebhookProjectRelMapper;

/**
 * @author scp
 * @date 2020/5/13
 * @description
 */
@Service
public class RelSendMessageC7nServiceImpl extends RelSendMessageServiceImpl implements RelSendMessageC7nService {

    private static final String NO_SEND_WEBHOOK = "NoSendWebHook";
    private static final String NO_SEND_WEB = "NoSendWeb";
    private static final String NO_SEND_EMAIL = "NoSendEmail";
    private static final String NO_SEND_SMS = "NoSendSms";

    @Autowired
    private TemplateServerService templateServerService;
    @Autowired
    private ReceiveSettingC7nMapper receiveSettingC7nMapper;
    @Autowired
    private MessageSettingC7nMapper messageSettingC7nMapper;
    @Autowired
    private WebhookProjectRelMapper webhookProjectRelMapper;


    protected void filterWebReceiver(MessageSender sender) {
        //如果有特殊标志则不发送
        if (isSend(sender, NO_SEND_WEB)) return;
        super.filterWebReceiver(sender);
        filterReceiver(sender, HmsgConstant.MessageType.WEB);
    }

    private boolean isSend(MessageSender sender, String noSendWeb) {
        if (!CollectionUtils.isEmpty(sender.getAdditionalInformation()) && !ObjectUtils.isEmpty(sender.getAdditionalInformation().get(noSendWeb))) {
            sender.setReceiverAddressList(Collections.EMPTY_LIST);
            return true;
        }
        return false;
    }

    protected void filterSmsReceiver(MessageSender sender) {
        if (isSend(sender, NO_SEND_SMS)) return;
        super.filterSmsReceiver(sender);
        filterReceiver(sender, HmsgConstant.MessageType.SMS);
    }

    protected void filterEmailReceiver(MessageSender sender) {
        if (isSend(sender, NO_SEND_EMAIL)) return;
        super.filterEmailReceiver(sender);
        filterReceiver(sender, HmsgConstant.MessageType.EMAIL);
    }


    protected void filterWebHookReceiver(MessageSender sender, List<WebHookSender> webHookSenderList) {
        if (!CollectionUtils.isEmpty(sender.getAdditionalInformation()) &&
                !ObjectUtils.isEmpty(sender.getAdditionalInformation().get(NO_SEND_WEBHOOK))) {
            webHookSenderList.clear();
        }
        super.filterWebHookReceiver(sender, webHookSenderList);
        webHookFilter(sender, webHookSenderList);
    }


    private void filterReceiver(MessageSender messageSender, String messageType) {
        TemplateServer templateServer = templateServerService.getTemplateServer(messageSender.getTenantId(), messageSender.getMessageCode());
        Long tempServerId = templateServer.getTempServerId();
        Long projectId = null;
        Long envId = null;
        String eventName = null;
        if (!CollectionUtils.isEmpty(messageSender.getAdditionalInformation()) &&
                !ObjectUtils.isEmpty(messageSender.getAdditionalInformation().get(MessageAdditionalType.PARAM_PROJECT_ID.getTypeName()))) {
            projectId = Long.valueOf(String.valueOf(messageSender.getAdditionalInformation().get(MessageAdditionalType.PARAM_PROJECT_ID.getTypeName())));
        }
        if (!CollectionUtils.isEmpty(messageSender.getAdditionalInformation()) &&
                !ObjectUtils.isEmpty(messageSender.getAdditionalInformation().get(MessageAdditionalType.PARAM_ENV_ID.getTypeName()))) {
            envId = Long.valueOf(String.valueOf(messageSender.getAdditionalInformation().get(MessageAdditionalType.PARAM_ENV_ID.getTypeName())));
        }
        if (!CollectionUtils.isEmpty(messageSender.getAdditionalInformation()) &&
                !ObjectUtils.isEmpty(messageSender.getAdditionalInformation().get(MessageAdditionalType.PARAM_EVENT_NAME.getTypeName()))) {
            eventName = String.valueOf(messageSender.getAdditionalInformation().get(MessageAdditionalType.PARAM_EVENT_NAME.getTypeName()));
        }
        List<Receiver> receiverList = messageSender.getReceiverAddressList();
        if (messageType.equals(HmsgConstant.MessageType.WEB) || messageType.equals(HmsgConstant.MessageType.EMAIL)) {
            receiveFilter(receiverList, tempServerId, projectId, messageType);
        }
        // 如果项目层未启动 则不发送 接受者为null
        if ((messageType.equals(HmsgConstant.MessageType.WEB) ||
                messageType.equals(HmsgConstant.MessageType.EMAIL) ||
                messageType.equals(HmsgConstant.MessageType.SMS))
                && messageSettingC7nMapper.selectProjectMessage().contains(messageSender.getMessageCode())) {
            if (projectFilter(messageSender, projectId, envId, eventName, messageType)) {
                receiverList.clear();
            }
        }
    }


    /**
     * 个人接收设置 消息过滤
     *
     * @param receiverAddressList
     * @param tempServerId
     * @param projectId
     */
    private void receiveFilter(List<Receiver> receiverAddressList, Long tempServerId, Long projectId, String messageType) {
        if (!CollectionUtils.isEmpty(receiverAddressList)) {
            List<Long> userIds = receiverAddressList.stream().map(Receiver::getUserId).collect(Collectors.toList());
            List<Long> removeUserIds = receiveSettingC7nMapper.selectByTemplateServerId(projectId, tempServerId, userIds, messageType);
            List<Receiver> removeUserList = receiverAddressList.stream().filter(t -> removeUserIds.contains(t.getUserId())).collect(Collectors.toList());
            receiverAddressList.removeAll(removeUserList);
        }
    }

    /**
     * 项目层 对应消息是否启用
     *
     * @param messageSender
     * @param projectId
     * @param envId
     * @param eventName
     * @param messageType
     * @return
     */
    private Boolean projectFilter(MessageSender messageSender, Long projectId, Long envId, String eventName, String messageType) {
        MessageSettingDTO messageSettingDTO = messageSettingC7nMapper.selectByParams(projectId, messageSender.getMessageCode(), envId, eventName, messageType);
        //如果项目下配置没有开启，则查询默认配置
        if (Objects.isNull(messageSettingDTO)) {
            messageSettingDTO = messageSettingC7nMapper.selectByParams(0L, messageSender.getMessageCode(), envId, eventName, messageType);
        }
        return ObjectUtils.isEmpty(messageSettingDTO);

    }


    /**
     * webhook过滤
     *
     * @param messageSender
     * @param webHookSenderList
     */
    private void webHookFilter(MessageSender messageSender, List<WebHookSender> webHookSenderList) {
        Long projectId = null;
        if (!CollectionUtils.isEmpty(messageSender.getAdditionalInformation()) &&
                !ObjectUtils.isEmpty(messageSender.getAdditionalInformation().get(MessageAdditionalType.PARAM_PROJECT_ID.getTypeName()))) {
            projectId = Long.valueOf(String.valueOf(messageSender.getAdditionalInformation().get(MessageAdditionalType.PARAM_PROJECT_ID.getTypeName())));
        }
        Long tenantId = null;
        if (!CollectionUtils.isEmpty(messageSender.getAdditionalInformation()) &&
                !ObjectUtils.isEmpty(messageSender.getAdditionalInformation().get(MessageAdditionalType.PARAM_TENANT_ID.getTypeName()))) {
            tenantId = Long.valueOf(String.valueOf(messageSender.getAdditionalInformation().get(MessageAdditionalType.PARAM_TENANT_ID.getTypeName())));
        }
        // 组织id和项目id都未传 不能发送
        if (tenantId == null && projectId == null) {
            webHookSenderList.clear();
        }
        List<String> webServerCodes;
        List<WebHookSender> senderList;
        if (!ObjectUtils.isEmpty(projectId)) {
            //项目成获取到应该发送的webhook地址
            webServerCodes = webhookProjectRelMapper.select(new WebhookProjectRelDTO().setProjectId(projectId)).stream().map(WebhookProjectRelDTO::getServerCode).collect(Collectors.toList());
            //获取本项目的webhook发送地址
            senderList = webHookSenderList.stream().filter(t -> webServerCodes.contains(t.getServerCode())).collect(Collectors.toList());
        } else {
            // 组织层获取到不应该发送的webhook地址
            webServerCodes = webhookProjectRelMapper.selectByTenantId(tenantId).stream().map(WebhookProjectRelDTO::getServerCode).collect(Collectors.toList());
            //获取本组织的webhook发送地址
            senderList = webHookSenderList.stream().filter(t -> !webServerCodes.contains(t.getServerCode())
                    && t.getTenantId().equals(Long.valueOf(String.valueOf(messageSender.getAdditionalInformation().get(MessageAdditionalType.PARAM_TENANT_ID.getTypeName()))))).collect(Collectors.toList());

        }
        webHookSenderList.clear();
        webHookSenderList.addAll(senderList);
        //如果是钉钉类型的消息清除接收者
        if (!CollectionUtils.isEmpty(webHookSenderList)) {
            for (WebHookSender webHookSender : webHookSenderList) {
                webHookSender.setLang("zh_CN");
                webHookSender.setReceiverAddressList(null);
            }
        }
        messageSender.setReceiverAddressList(null);
    }

}
