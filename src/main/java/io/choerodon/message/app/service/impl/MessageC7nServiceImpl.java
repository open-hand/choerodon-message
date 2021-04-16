package io.choerodon.message.app.service.impl;

import java.util.*;
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

import io.choerodon.core.domain.Page;
import io.choerodon.message.api.vo.CustomEmailSendInfoVO;
import io.choerodon.message.api.vo.UserVO;
import io.choerodon.message.app.service.MessageC7nService;
import io.choerodon.message.infra.dto.MessageC7nDTO;
import io.choerodon.message.infra.dto.iam.TenantDTO;
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
    public Page<MessageC7nDTO> listMessage(String status, String failedReason, String messageName, String params, PageRequest pageRequest) {
        return PageHelper.doPage(pageRequest, () -> messageC7nMapper.listEmailMessage(status, failedReason, messageName, params));
    }

    @Override
    @ProcessLovValue
    public Page<MessageC7nDTO> listWebHooks(String status, String webhookAddress, String templateType, String failedReason, String params, PageRequest pageRequest) {
        return PageHelper.doPage(pageRequest, () -> messageC7nMapper.listWebHooks(status, webhookAddress, templateType, failedReason, params));
    }

    @Override
    @Async
    public void sendCustomEmail(CustomEmailSendInfoVO customEmailSendInfoVO) {
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
        MessageSender messageSender = (new MessageSender()).setTenantId(TenantDTO.DEFAULT_TENANT_ID).setMessageCode("CUSTOM").setServerCode(DEFAULT_SERVER_CODE).setReceiverAddressList(receiverAddressList).setCcList(ccList).setBccList(null).setMessage(message);
        if (customEmailSendInfoVO.getFile() != null) {
            Attachment attachment = new Attachment();
            // data:application/zip;base64,base64数据
            String[] base64StrInfo = customEmailSendInfoVO.getFile().split(",");
            String fileType = base64StrInfo[0].split("/")[1].split(";")[0];
            String base64Data = base64StrInfo[1];
            Base64.Decoder decoder = Base64.getDecoder();

            attachment.setFile(decoder.decode(base64Data));
            attachment.setFileName(customEmailSendInfoVO.getFilename() + "." + fileType);
            messageSender.setAttachmentList(Collections.singletonList(attachment));
        }
        emailSendService.sendMessage(TenantDTO.DEFAULT_TENANT_ID, messageSender);
    }
}
