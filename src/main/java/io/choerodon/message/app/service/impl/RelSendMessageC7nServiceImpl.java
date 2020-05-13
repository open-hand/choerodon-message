package io.choerodon.message.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.hzero.boot.message.entity.Attachment;
import org.hzero.boot.message.entity.Message;
import org.hzero.boot.message.entity.MessageSender;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.core.base.BaseConstants;
import org.hzero.message.app.service.*;
import org.hzero.message.app.service.impl.RelSendMessageServiceImpl;
import org.hzero.message.domain.entity.TemplateServer;
import org.hzero.message.domain.entity.TemplateServerLine;
import org.hzero.message.domain.repository.TemplateServerLineRepository;
import org.hzero.message.domain.repository.TemplateServerRepository;
import org.hzero.message.infra.constant.HmsgConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import io.choerodon.core.enums.MessageAdditionalType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.message.app.service.RelSendMessageC7nService;
import io.choerodon.message.infra.dto.MessageSettingDTO;
import io.choerodon.message.infra.dto.WebhookProjectRelDTO;
import io.choerodon.message.infra.dto.iam.ProjectDTO;
import io.choerodon.message.infra.feign.operator.IamClientOperator;
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

    @Autowired
    private TemplateServerService templateServerService;
    @Autowired
    private TemplateServerLineRepository templateServerLineRepository;
    @Autowired
    private MessageReceiverService messageReceiverService;
    @Autowired
    private ReceiveSettingC7nMapper receiveSettingC7nMapper;
    @Autowired
    private MessageSettingC7nMapper messageSettingC7nMapper;
    @Autowired
    private IamClientOperator iamClientOperator;
    @Autowired
    private WebhookProjectRelMapper webhookProjectRelMapper;

    @Override
    public Map<String, Integer> relSendMessage(MessageSender messageSender) {
        // 检查接收人列表
        messageSender = messageReceiverService.queryReceiver(messageSender);
        TemplateServer templateServer = templateServerService.getTemplateServer(messageSender.getTenantId(), messageSender.getMessageCode());
        Assert.notNull(templateServer, BaseConstants.ErrorCode.DATA_NOT_EXISTS);

        // 检查用户接收配置
        messageSender.setReceiverAddressList(filterUserReceiverConfig(messageSender.getReceiverAddressList(),
                StringUtils.hasText(messageSender.getReceiveConfigCode()) ?
                        messageSender.getReceiveConfigCode() : templateServer.getMessageCode(), templateServer.getTenantId()));
        Assert.isTrue(Objects.equals(templateServer.getEnabledFlag(), BaseConstants.Flag.YES), BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        Map<String, List<TemplateServerLine>> serverLineMap = templateServerLineRepository.enabledTemplateServerLine(templateServer.getTempServerId(), templateServer.getTenantId())
                .stream().collect(Collectors.groupingBy(TemplateServerLine::getTypeCode));

        return sendMessageC7n(serverLineMap, messageSender, templateServer.getTempServerId());
    }

    private Map<String, Integer> sendMessageC7n(Map<String, List<TemplateServerLine>> serverLineMap, MessageSender messageSender, Long tempServerId) {
        Map<String, Integer> result = new HashMap<>(4);
        List<Receiver> receiverList = messageSender.getReceiverAddressList();
        Long projectId = (Long) messageSender.getAdditionalInformation().get(MessageAdditionalType.PARAM_PROJECT_ID.getTypeName());
        Long envId = (Long) messageSender.getAdditionalInformation().get(MessageAdditionalType.PARAM_ENV_ID.getTypeName());
        String eventName = (String) messageSender.getAdditionalInformation().get(MessageAdditionalType.PARAM_EVENT_NAME.getTypeName());
        // 每种类型的模板只能指定一个
        // 模板允许站内消息 且 (不指定发送方式且userId不为空 或 发送方式中指定了站内消息)
        if (serverLineMap.containsKey(HmsgConstant.MessageType.WEB) && sendEnable(messageSender.getTypeCodeList(), HmsgConstant.MessageType.WEB)) {
            receiveFilter(receiverList, tempServerId, projectId, HmsgConstant.MessageType.WEB);
            if (!CollectionUtils.isEmpty(receiverList) && projectFilter(messageSender, projectId, envId, eventName, HmsgConstant.MessageType.WEB)) {
                sendWeb(serverLineMap, receiverList, messageSender.getTenantId(), messageSender.getLang(), messageSender.getArgs(), result, messageSender.getMessageMap());
            }
        }
        // 模板允许短信 且 (不指定发送方式且phone不为空 或 发送方式中指定了短信)
        if (serverLineMap.containsKey(HmsgConstant.MessageType.SMS) && sendEnable(messageSender.getTypeCodeList(), HmsgConstant.MessageType.SMS)) {
            if (projectFilter(messageSender, projectId, envId, eventName, HmsgConstant.MessageType.SMS)) {
                sendSms(serverLineMap, receiverList, messageSender.getTenantId(), messageSender.getLang(), messageSender.getArgs(), result, messageSender.getMessageMap());
            }
        }
        // 模板允许邮件 且 (不指定发送方式且email不为空 或 发送方式中指定了邮件)
        if (serverLineMap.containsKey(HmsgConstant.MessageType.EMAIL) && sendEnable(messageSender.getTypeCodeList(), HmsgConstant.MessageType.EMAIL)) {
            receiveFilter(receiverList, tempServerId, projectId, HmsgConstant.MessageType.EMAIL);
            if (!CollectionUtils.isEmpty(receiverList) && projectFilter(messageSender, projectId, envId, eventName, HmsgConstant.MessageType.EMAIL)) {
                sendEmail(serverLineMap, receiverList, messageSender.getTenantId(), messageSender.getLang(), messageSender.getArgs(), result, messageSender.getMessageMap(), messageSender.getAttachmentList(), messageSender.getCcList(), messageSender.getBccList(), messageSender.getBatchSend());
            }
        }
        // 模板允许语音 且 (不指定发送方式且phone不为空 或 发送方式中指定了与语音)
        if (serverLineMap.containsKey(HmsgConstant.MessageType.CALL) && sendEnable(messageSender.getTypeCodeList(), HmsgConstant.MessageType.CALL)) {
            sendCall(serverLineMap, receiverList, messageSender.getTenantId(), messageSender.getLang(), messageSender.getArgs(), result, messageSender.getMessageMap());
        }
        if (result.size() == 0) {
            throw new CommonException(HmsgConstant.ErrorCode.MISSING_RECIPIENT);
        }
        return result;
    }


    /**
     * 个人接收设置 消息过滤
     *
     * @param receiverAddressList
     * @param tempServerId
     * @param projectId
     */
    private void receiveFilter(List<Receiver> receiverAddressList, Long tempServerId, Long projectId, String messageType) {
        List<Long> userIds = receiverAddressList.stream().map(Receiver::getUserId).collect(Collectors.toList());
        List<Long> removeUserIds = receiveSettingC7nMapper.selectByTemplateServerId(projectId, tempServerId, userIds, messageType);
        List<Receiver> removeUserList = receiverAddressList.stream().filter(t -> removeUserIds.contains(t.getUserId())).collect(Collectors.toList());
        receiverAddressList.removeAll(removeUserList);
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
        return !ObjectUtils.isEmpty(messageSettingDTO);

    }


    /**
     * webhook过滤
     * @param serverLineMap
     * @param tenantId
     * @param projectId
     */
    private void webHookFilter(Map<String, List<TemplateServerLine>> serverLineMap, Long tenantId, Long projectId) {
        Map<String, List<TemplateServerLine>> newMap = new HashMap<>();
        if (!ObjectUtils.isEmpty(projectId)) {
            List<String> webServerCodes = webhookProjectRelMapper.select(new WebhookProjectRelDTO().setProjectId(projectId)).stream().map(WebhookProjectRelDTO::getServerCode).collect(Collectors.toList());
            for (Map.Entry<String, List<TemplateServerLine>> entry : serverLineMap.entrySet()) {
                List<TemplateServerLine> values = entry.getValue().stream().filter(t -> webServerCodes.contains(t.getServerCode())).collect(Collectors.toList());
                newMap.put(entry.getKey(), values);
            }
        } else {
            List<String> webServerCodes = webhookProjectRelMapper.select(new WebhookProjectRelDTO().setTenantId(tenantId)).stream().map(WebhookProjectRelDTO::getServerCode).collect(Collectors.toList());
            for (Map.Entry<String, List<TemplateServerLine>> entry : serverLineMap.entrySet()) {
                List<TemplateServerLine> values = entry.getValue().stream().filter(t -> !webServerCodes.contains(t.getServerCode())).collect(Collectors.toList());
                newMap.put(entry.getKey(), values);
            }

        }
        serverLineMap.clear();
        serverLineMap.putAll(newMap);
    }


}
