package io.choerodon.message.app.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.hzero.boot.message.entity.MessageSender;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.message.app.service.MessageReceiverService;
import org.hzero.message.app.service.TemplateServerService;
import org.hzero.message.app.service.impl.RelSendMessageServiceImpl;
import org.hzero.message.domain.entity.TemplateServer;
import org.hzero.message.domain.entity.TemplateServerLine;
import org.hzero.message.domain.entity.UserReceiveConfig;
import org.hzero.message.domain.repository.TemplateServerLineRepository;
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
    private ReceiveSettingC7nMapper receiveSettingC7nMapper;
    @Autowired
    private MessageSettingC7nMapper messageSettingC7nMapper;
    @Autowired
    private WebhookProjectRelMapper webhookProjectRelMapper;


    protected void filterWebReceiver(MessageSender sender) {
        super.filterWebReceiver(sender);
        filterReceiver(sender, HmsgConstant.MessageType.WEB);
    }

    protected void filterSmsReceiver(MessageSender sender) {
        super.filterSmsReceiver(sender);
        filterReceiver(sender, HmsgConstant.MessageType.SMS);
    }

    protected void filterEmailReceiver(MessageSender sender) {
        super.filterEmailReceiver(sender);
        filterReceiver(sender, HmsgConstant.MessageType.EMAIL);
    }


    protected void filterWebHookReceiver(MessageSender sender, Map<String, List<TemplateServerLine>> serverLineMap) {
        super.filterWebHookReceiver(sender, serverLineMap);
        webHookFilter(sender, serverLineMap);
    }


    private void filterReceiver(MessageSender messageSender, String messageType) {
        Long tempServerId = templateServerService.getTemplateServer(messageSender.getTenantId(), messageSender.getMessageCode()).getTempServerId();
        Long projectId = (Long) messageSender.getAdditionalInformation().get(MessageAdditionalType.PARAM_PROJECT_ID.getTypeName());
        Long envId = (Long) messageSender.getAdditionalInformation().get(MessageAdditionalType.PARAM_ENV_ID.getTypeName());
        String eventName = (String) messageSender.getAdditionalInformation().get(MessageAdditionalType.PARAM_EVENT_NAME.getTypeName());
        List<Receiver> receiverList = messageSender.getReceiverAddressList();
        if (messageType.equals(HmsgConstant.MessageType.WEB) || messageType.equals(HmsgConstant.MessageType.EMAIL)) {
            receiveFilter(receiverList, tempServerId, projectId, messageType);
        }
        // 如果项目层未启动 则不发送 接受者为null
        if (messageType.equals(HmsgConstant.MessageType.WEB) ||
                messageType.equals(HmsgConstant.MessageType.EMAIL) ||
                messageType.equals(HmsgConstant.MessageType.SMS)) {
            if (!projectFilter(messageSender, projectId, envId, eventName, messageType)) {
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
     *
     * @param messageSender
     * @param serverLineMap
     */
    private void webHookFilter(MessageSender messageSender, Map<String, List<TemplateServerLine>> serverLineMap) {
        Long projectId = (Long) messageSender.getAdditionalInformation().get(MessageAdditionalType.PARAM_PROJECT_ID.getTypeName());
        Map<String, List<TemplateServerLine>> newMap = new HashMap<>();
        if (!ObjectUtils.isEmpty(projectId)) {
            List<String> webServerCodes = webhookProjectRelMapper.select(new WebhookProjectRelDTO().setProjectId(projectId)).stream().map(WebhookProjectRelDTO::getServerCode).collect(Collectors.toList());
            for (Map.Entry<String, List<TemplateServerLine>> entry : serverLineMap.entrySet()) {
                List<TemplateServerLine> values = entry.getValue().stream().filter(t -> webServerCodes.contains(t.getServerCode())).collect(Collectors.toList());
                newMap.put(entry.getKey(), values);
            }
        } else {
            List<String> webServerCodes = webhookProjectRelMapper.select(new WebhookProjectRelDTO().setTenantId(messageSender.getTenantId())).stream().map(WebhookProjectRelDTO::getServerCode).collect(Collectors.toList());
            for (Map.Entry<String, List<TemplateServerLine>> entry : serverLineMap.entrySet()) {
                List<TemplateServerLine> values = entry.getValue().stream().filter(t -> !webServerCodes.contains(t.getServerCode())).collect(Collectors.toList());
                newMap.put(entry.getKey(), values);
            }

        }
        serverLineMap.clear();
        serverLineMap.putAll(newMap);
    }


}
