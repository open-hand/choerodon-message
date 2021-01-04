package io.choerodon.message.app.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.hzero.boot.message.entity.Attachment;
import org.hzero.boot.message.entity.Message;
import org.hzero.boot.message.entity.MessageSender;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.message.app.service.EmailSendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.core.domain.Page;
import io.choerodon.message.api.vo.CustomEmailSendInfoVO;
import io.choerodon.message.api.vo.UserVO;
import io.choerodon.message.app.service.MessageC7nService;
import io.choerodon.message.infra.dto.MessageC7nDTO;
import io.choerodon.message.infra.feign.operator.IamClientOperator;
import io.choerodon.message.infra.mapper.MessageC7nMapper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author lihao
 */
@Service
public class MessageC7nServiceImpl implements MessageC7nService {

    private static final String DEFAULT_SERVER_CODE = "CHOERODON-EMAIL";

    @Autowired
    private MessageC7nMapper messageC7nMapper;
    @Autowired
    private EmailSendService emailSendService;
    @Autowired
    private IamClientOperator iamClientOperator;

    @Override
    @ProcessLovValue
    public Page<MessageC7nDTO> listMessage(String status, String receiveEmail, String templateType, String failedReason, String params, PageRequest pageRequest) {
        return PageHelper.doPage(pageRequest, () -> messageC7nMapper.listMessage(status, receiveEmail, templateType, failedReason, params));
    }

    @Override
    @ProcessLovValue
    public Page<MessageC7nDTO> listWebHooks(String status, String webhookAddress, String templateType, String failedReason, String params, PageRequest pageRequest) {
        return PageHelper.doPage(pageRequest, () -> messageC7nMapper.listWebHooks(status, webhookAddress, templateType, failedReason, params));
    }

    @Override
    @Async
    public void sendCustomEmail(CustomEmailSendInfoVO customEmailSendInfoVO, MultipartFile file) {
        List<UserVO> receiverUsers = iamClientOperator.listUsersByIds(customEmailSendInfoVO.getReceiverIdList(), false);
        List<UserVO> ccUsers = iamClientOperator.listUsersByIds(customEmailSendInfoVO.getCcIdList(), false);

        List<Receiver> receiverAddressList = new ArrayList<>();

        if (!CollectionUtils.isEmpty(receiverUsers)) {
            receiverAddressList = receiverUsers.stream().map(user -> {
                Receiver receiver = new Receiver();
                receiver.setUserId(user.getId());
                // 发送邮件消息时 必填
                receiver.setEmail(user.getEmail());
                // 发送短信消息 必填
                receiver.setPhone(user.getPhone());
                // 必填
                receiver.setTargetUserTenantId(user.getOrganizationId());
                return receiver;
            }).collect(Collectors.toList());
        }
        List<String> ccList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(ccUsers)) {
            ccList = ccUsers.stream().filter(userVO -> userVO.getEmail() != null).map(UserVO::getEmail).collect(Collectors.toList());
        }

        Message message = (new Message()).setServerCode(DEFAULT_SERVER_CODE).setMessageTypeCode("EMAIL").setTemplateCode("CUSTOM").setLang("zh_CN").setTenantId(0L).setSubject(customEmailSendInfoVO.getSubject()).setContent(customEmailSendInfoVO.getContent());
        MessageSender messageSender = (new MessageSender()).setTenantId(0L).setMessageCode("CUSTOM").setServerCode(DEFAULT_SERVER_CODE).setReceiverAddressList(receiverAddressList).setCcList(ccList).setBccList(null).setMessage(message);
        Attachment attachment = new Attachment();
        attachment.setFileName(file.getName());
        try {
            attachment.setFile(file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        messageSender.setAttachmentList(Collections.singletonList(attachment));
        emailSendService.sendMessage(messageSender);
    }
}
