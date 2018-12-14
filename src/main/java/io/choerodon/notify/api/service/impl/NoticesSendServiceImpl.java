package io.choerodon.notify.api.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.notify.api.dto.EmailConfigDTO;
import io.choerodon.notify.api.dto.NoticeSendDTO;
import io.choerodon.notify.api.dto.UserDTO;
import io.choerodon.notify.api.pojo.MessageType;
import io.choerodon.notify.api.service.EmailSendService;
import io.choerodon.notify.api.service.NoticesSendService;
import io.choerodon.notify.api.service.WebSocketSendService;
import io.choerodon.notify.domain.ReceiveSetting;
import io.choerodon.notify.domain.SendSetting;
import io.choerodon.notify.infra.feign.UserFeignClient;
import io.choerodon.notify.infra.mapper.ReceiveSettingMapper;
import io.choerodon.notify.infra.mapper.SendSettingMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NoticesSendServiceImpl implements NoticesSendService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NoticesSendServiceImpl.class);

    private EmailSendService emailSendService;

    private WebSocketSendService webSocketSendService;
    private ReceiveSettingMapper receiveSettingMapper;
    private SendSettingMapper sendSettingMapper;
    private UserFeignClient userFeignClient;

    public NoticesSendServiceImpl(EmailSendService emailSendService,
                                  @Qualifier("pmWsSendService") WebSocketSendService webSocketSendService,
                                  ReceiveSettingMapper receiveSettingMapper,
                                  SendSettingMapper sendSettingMapper,
                                  UserFeignClient userFeignClient) {
        this.emailSendService = emailSendService;
        this.webSocketSendService = webSocketSendService;
        this.receiveSettingMapper = receiveSettingMapper;
        this.sendSettingMapper = sendSettingMapper;
        this.userFeignClient = userFeignClient;
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
        SendSetting sendSetting = sendSettingMapper.selectOne(new SendSetting(dto.getCode()));
        if (dto.getCode() == null || sendSetting == null) {
            LOGGER.info("no sendSetting : {}, can`t send notice. sendUsers: {}", dto.getCode(), dto.getTargetUsers());
            return;
        }
        boolean haveEmailTemplate = sendSetting.getEmailTemplateId() != null;
        boolean havePmTemplate = sendSetting.getPmTemplateId() != null;
        //如果没有任何模板，则不发起feign调用
        if (!haveEmailTemplate && !havePmTemplate) {
            LOGGER.info("sendsetting '{}' no opposite email template and pm template, cann`t send notice. sendUsers: {}",
                    dto.getCode(), dto.getTargetUsers());
            return;
        }
        //取得需要发送通知用户
        Set<UserDTO> users = getNeedSendUsers(dto);
        trySendEmail(dto, sendSetting, users, haveEmailTemplate);
        trySendSiteMessage(dto, sendSetting, users, havePmTemplate);
    }

    private void trySendEmail(NoticeSendDTO dto, SendSetting sendSetting, final Set<UserDTO> users, final boolean haveEmailTemplate) {
        //捕获异常,防止邮件发送失败，影响站内信发送
        try {
            if (haveEmailTemplate) {
                //得到需要发送邮件的用户
                Set<UserDTO> needSendEmailUsers = getNeedReceiveNoticeTargetUsers(dto, sendSetting, users, MessageType.EMAIL);
                emailSendService.sendEmail(dto.getCode(), dto.getParams(), needSendEmailUsers, sendSetting);
            } else {
                LOGGER.info("sendsetting '{}' no opposite email template, cann`t send email. sendUsers: {}",
                        dto.getCode(), dto.getTargetUsers());
            }
        } catch (CommonException e) {
            LOGGER.info("send email failed!", e);
        }
    }

    private void trySendSiteMessage(NoticeSendDTO dto, SendSetting sendSetting, final Set<UserDTO> users, final boolean havePmTemplate) {
        try {
            if (havePmTemplate) {
                //得到需要发送站内信的用户
                Set<UserDTO> needSendPmUsers = getNeedReceiveNoticeTargetUsers(dto, sendSetting, users, MessageType.PM);
                webSocketSendService.sendSiteMessage(dto.getCode(), dto.getParams(), needSendPmUsers,
                        Optional.ofNullable(dto.getFromUser()).map(NoticeSendDTO.User::getId).orElse(null), sendSetting);
            } else {
                LOGGER.info("sendsetting '{}' no opposite pm template ,cann`t send pm. sendUsers: {}",
                        dto.getCode(), dto.getTargetUsers());
            }
        } catch (CommonException e) {
            LOGGER.info("send station letter failed!", e);
        }
    }

    /**
     * 取得需要发送通知的用户
     */
    private Set<UserDTO> getNeedSendUsers(final NoticeSendDTO dto) {
        LOGGER.info("start:get "+dto.getTargetUsers().size()+" target users");
        Set<UserDTO> users = new HashSet<>();
        //取得User中id不为空的id，将发起feign调用
        Set<Long> needQueryUserIds = dto.getTargetUsers().stream().filter(user -> user.getId() != null).map(NoticeSendDTO.User::getId).collect(Collectors.toSet());
        if (!needQueryUserIds.isEmpty()) {
            Long[] userIds = needQueryUserIds.toArray(new Long[0]);
            //TODO 如果用户太多，需要多次查询
            users.addAll(userFeignClient.listUsersByIds(userIds).getBody());
        }
        //取得User中id为空且email不为空的email，将发起feign调用
        Set<String> needQueryEmails = dto.getTargetUsers().stream().filter(user -> user.getId() == null && user.getEmail() != null).map(NoticeSendDTO.User::getEmail).collect(Collectors.toSet());
        if (!needQueryEmails.isEmpty()) {
            String[] emails = needQueryEmails.toArray(new String[0]);
            //注册组织时没有真正的用户，只有email,因此伪造一个UserDTO
            //TODO 如果用户太多，需要多次查询
            List<UserDTO> emailUsers = userFeignClient.listUsersByEmails(emails).getBody();
            LOGGER.info("get "+emailUsers.size()+" target users,only have email's info");
            Set<String> queryEmails = emailUsers.stream().map(UserDTO::getEmail).collect(Collectors.toSet());
            List<UserDTO> onlyEmailUsers = needQueryEmails.stream().filter(s -> !queryEmails.contains(s)).map(email -> {
                UserDTO userDTO = new UserDTO();
                userDTO.setEmail(email);
                return userDTO;
            }).collect(Collectors.toList());
            users.addAll(emailUsers);
            users.addAll(onlyEmailUsers);
        }
        LOGGER.info("end:get "+users.size()+" target users");
        return users.stream().filter(userDTO -> userDTO.getId() != null).collect(Collectors.toSet());
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
