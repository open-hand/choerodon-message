package io.choerodon.notify.api.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import io.choerodon.notify.api.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import io.choerodon.core.exception.CommonException;
import io.choerodon.notify.api.dto.EmailConfigDTO;
import io.choerodon.notify.api.dto.NoticeSendDTO;
import io.choerodon.notify.api.dto.UserDTO;
import io.choerodon.notify.api.pojo.MessageType;
import io.choerodon.notify.domain.ReceiveSetting;
import io.choerodon.notify.domain.SendSetting;
import io.choerodon.notify.infra.enums.SenderType;
import io.choerodon.notify.infra.feign.UserFeignClient;
import io.choerodon.notify.infra.mapper.ReceiveSettingMapper;
import io.choerodon.notify.infra.mapper.SendSettingMapper;

@Service
public class NoticesSendServiceImpl implements NoticesSendService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NoticesSendServiceImpl.class);

    private EmailSendService emailSendService;

    private WebSocketSendService webSocketSendService;
    private WebHookService webHookService;
    private ReceiveSettingMapper receiveSettingMapper;
    private SendSettingMapper sendSettingMapper;
    private UserFeignClient userFeignClient;

    private SmsService smsService;

    public NoticesSendServiceImpl(EmailSendService emailSendService,
                                  @Qualifier("pmWsSendService") WebSocketSendService webSocketSendService,
                                  WebHookService webHookService, ReceiveSettingMapper receiveSettingMapper,
                                  SendSettingMapper sendSettingMapper,
                                  UserFeignClient userFeignClient,
                                  SmsService smsService) {
        this.emailSendService = emailSendService;
        this.webSocketSendService = webSocketSendService;
        this.webHookService = webHookService;
        this.receiveSettingMapper = receiveSettingMapper;
        this.sendSettingMapper = sendSettingMapper;
        this.userFeignClient = userFeignClient;
        this.smsService = smsService;
    }

    //单元测试
    public void setUserFeignClient(UserFeignClient userFeignClient) {
        this.userFeignClient = userFeignClient;
    }

    @Override
    public void testEmailConnect(EmailConfigDTO config) {
        emailSendService.testEmailConnect(config);
    }

    @Override
    public void sendNotice(NoticeSendDTO dto) {
        if (dto.isSendingSMS()) {
            smsService.send(dto);
        }
        SendSetting sendSetting = sendSettingMapper.selectOne(new SendSetting(dto.getCode()));
        if (dto.getCode() == null || sendSetting == null) {
            LOGGER.warn("no sendSetting : {}, can`t send notice.", dto.getCode());
            return;
        }
        boolean haveEmailTemplate = sendSetting.getEmailTemplateId() != null;
        boolean havePmTemplate = sendSetting.getPmTemplateId() != null;
        boolean haveSMSTemplate = sendSetting.getSmsTemplateId() != null;
        boolean enableWebHook = Boolean.TRUE.equals(sendSetting.getWhEnabledFlag());
        // 如果消息服务未启用，不发送通知
        if (sendSetting.getEnabled() == null || !sendSetting.getEnabled()) {
            LOGGER.warn("sendSetting '{}' disabled, can`t send notice.", dto.getCode());
            return;
        }
        // 如果没有任何模板，则不发起feign调用
        if (!haveEmailTemplate && !havePmTemplate && !haveSMSTemplate && !enableWebHook) {
            LOGGER.warn("sendSetting '{}' no opposite email template and pm template and sms template, can`t send notice.", dto.getCode());
            return;
        }
        boolean doCustomizedSending = (dto.getCustomizedSendingTypes() != null && !dto.getCustomizedSendingTypes().isEmpty());

        // 取得需要发送通知用户
        if (ObjectUtils.isEmpty(dto.getTargetUsers())) {
            return;
        }
        Set<UserDTO> users = getNeedSendUsers(dto);
        if (doCustomizedSending) {
            if (dto.isSendingEmail()) {
                trySendEmail(dto, sendSetting, users, haveEmailTemplate);
            }
            if (dto.isSendingSiteMessage()) {
                trySendSiteMessage(dto, sendSetting, users, havePmTemplate);
            }
            if (dto.isSendingWebHook() && Boolean.TRUE.equals(sendSetting.getWhEnabledFlag())){
                webHookService.trySendWebHook(dto, sendSetting);
            }
        } else {
            trySendEmail(dto, sendSetting, users, haveEmailTemplate);
            trySendSiteMessage(dto, sendSetting, users, havePmTemplate);
            if (Boolean.TRUE.equals(sendSetting.getWhEnabledFlag())){
                webHookService.trySendWebHook(dto, sendSetting);
            }
        }
    }



    private void trySendEmail(NoticeSendDTO dto, SendSetting sendSetting, final Set<UserDTO> users, final boolean haveEmailTemplate) {
        // 捕获异常,防止邮件发送失败，影响站内信发送
        try {
            if (haveEmailTemplate) {
                // 得到需要发送邮件的用户
                Set<UserDTO> needSendEmailUsers = getNeedReceiveNoticeTargetUsers(dto, sendSetting, users, MessageType.EMAIL);
                emailSendService.sendEmail(dto.getCode(), dto.getParams(), needSendEmailUsers, sendSetting);
            } else {
                LOGGER.warn("sendSetting '{}' no opposite email template, can`t send email.", dto.getCode());
            }
        } catch (CommonException e) {
            LOGGER.error("send email failed!", e);
        }
    }

    private void trySendSiteMessage(NoticeSendDTO dto, SendSetting sendSetting, final Set<UserDTO> users, final boolean havePmTemplate) {
        try {
            if (havePmTemplate) {
                //得到需要发送站内信的用户
                Set<UserDTO> needSendPmUsers = getNeedReceiveNoticeTargetUsers(dto, sendSetting, users, MessageType.PM);
                Map<String, Long> sender = new HashMap<>(5);
                String senderType = getSenderDetail(dto, sender, sendSetting);
                webSocketSendService.sendSiteMessage(dto.getCode(), dto.getParams(), needSendPmUsers,
                        sender.get(senderType), senderType, sendSetting);
            } else {
                LOGGER.warn("sendSetting '{}' no opposite pm template, can`t send pm.", dto.getCode());
            }
        } catch (CommonException e) {
            LOGGER.error("send station letter failed!", e);
        }
    }

    /**
     * sender type is user/project/organization/site
     *
     * @param dto NoticeSendDTO
     * @return sender type
     */
    private String getSenderDetail(NoticeSendDTO dto, Map<String, Long> map, SendSetting sendSetting) {
        NoticeSendDTO.User user = dto.getFromUser();
        //设置默认发送者为平台
        String senderType = SenderType.SITE.value();
        map.put(senderType, 0L);
        if (user == null) {
            String sendSettingLevel = sendSetting.getLevel();
            if (SenderType.ORGANIZATION.value().equals(sendSettingLevel)) {
                senderType = sendSettingLevel;
                Long organizationId = dto.getSourceId();
                map.put(senderType, organizationId);
            } else if (SenderType.PROJECT.value().equals(sendSettingLevel)) {
                senderType = sendSettingLevel;
                Long projectId = dto.getSourceId();
                map.put(senderType, projectId);
            }
        } else {
            senderType = SenderType.USER.value();
            map.put(senderType, user.getId());
        }
        return senderType;
    }

    /**
     * 取得需要发送通知的用户
     */
    private Set<UserDTO> getNeedSendUsers(final NoticeSendDTO dto) {
        Set<UserDTO> users = new HashSet<>();
        // 取得User中id不为空的id，将发起feign调用
        Set<Long> needQueryUserIds = dto.getTargetUsers().stream().filter(user -> user.getId() != null).map(NoticeSendDTO.User::getId).collect(Collectors.toSet());
        if (!needQueryUserIds.isEmpty()) {
            Long[] userIds = needQueryUserIds.toArray(new Long[0]);
            //TODO 如果用户太多，需要多次查询
            users.addAll(userFeignClient.listUsersByIds(userIds).getBody());
        }
        // 取得User中id为空且email不为空的email，将发起feign调用
        Set<String> needQueryEmails = dto.getTargetUsers().stream().filter(user -> user.getId() == null && user.getEmail() != null).map(NoticeSendDTO.User::getEmail).collect(Collectors.toSet());
        if (!needQueryEmails.isEmpty()) {
            String[] emails = needQueryEmails.toArray(new String[0]);
            // 注册组织时没有真正的用户，只有email,因此伪造一个UserDTO
            //TODO 如果用户太多，需要多次查询
            List<UserDTO> emailUsers = userFeignClient.listUsersByEmails(emails).getBody();
            Set<String> queryEmails = emailUsers.stream().map(UserDTO::getEmail).collect(Collectors.toSet());
            List<UserDTO> onlyEmailUsers = needQueryEmails.stream().filter(s -> !queryEmails.contains(s)).map(email -> {
                UserDTO userDTO = new UserDTO();
                userDTO.setEmail(email);
                return userDTO;
            }).collect(Collectors.toList());
            users.addAll(emailUsers);
            users.addAll(onlyEmailUsers);
        }
        return users;
    }


    /**
     * 如果发送设置允许配置通知
     * 则得到没有禁用接收通知的用户
     * 否则得到全部用户
     */
    private Set<UserDTO> getNeedReceiveNoticeTargetUsers(final NoticeSendDTO dto, final SendSetting sendSetting, final Set<UserDTO> users, final MessageType type) {
        if (!sendSetting.getAllowConfig()) {
            return users;
        }
        return users.stream().filter(user -> {
            ReceiveSetting setting = new ReceiveSetting(sendSetting.getId(), type.getValue(), dto.getSourceId(), sendSetting.getLevel(), user.getId());
            return receiveSettingMapper.selectCount(setting) == 0;
        }).collect(Collectors.toSet());
    }
}
