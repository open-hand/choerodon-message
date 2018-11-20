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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
            LOGGER.info("no sendsetting,cann`t send notice.");
            return;
        }
        boolean haveEmailTemplate = sendSetting.getEmailTemplateId() != null;
        boolean havePmTemplate = sendSetting.getPmTemplateId() != null;
        //如果没有任何模板，则不发起feign调用
        if (!haveEmailTemplate && !havePmTemplate) {
            LOGGER.info("sendsetting no opposite email template and pm template,cann`t send notice");
            return;
        }
        trySendEmail(dto, sendSetting, haveEmailTemplate);
        trySendSiteMessage(dto, sendSetting, havePmTemplate);
    }

    /**
     * 取得需要发送邮件的emails
     *
     * @param dto
     * @param sendSetting
     * @return
     */
    private Set<String> getNeedSendEmail(NoticeSendDTO dto, SendSetting sendSetting) {
        List<UserDTO> needQueryUserDTOS = new ArrayList<>();
        //取得User中email为空的id
        Set<Long> needQueryUserIds = dto.getTargetUsers().stream().filter(user -> user.getEmail() == null).map(NoticeSendDTO.User::getId).collect(Collectors.toSet());
        //取得User中email不为空的email
        Set<String> existsEmails = dto.getTargetUsers().stream().filter(user -> user.getEmail() != null).map(NoticeSendDTO.User::getEmail).collect(Collectors.toSet());
        Long[] userIds = needQueryUserIds.toArray(new Long[needQueryUserIds.size()]);
        if (!needQueryUserIds.isEmpty()) {
            needQueryUserDTOS = userFeignClient.listUsersByIds(userIds).getBody();
            //取得未禁用接收邮件通知或者不允许禁用接收邮件通知的所有用户
            List<UserDTO> emailUserDTOS = getEmailTargetUsers(dto, sendSetting, needQueryUserDTOS);
            Set<String> emails = emailUserDTOS.stream().map(UserDTO::getEmail).filter(t -> t != null).collect(Collectors.toSet());
            existsEmails.addAll(emails);
        }
        return existsEmails;
    }

    private void trySendSiteMessage(NoticeSendDTO dto, SendSetting sendSetting, final boolean havePmTemplate) {
        try {
            if (havePmTemplate) {
                //取得未禁用接收站内信通知或者不允许禁用接收站内信通知的所有用户
                List<NoticeSendDTO.User> pmUserDTOS = getPmTargetUsers(dto, sendSetting, dto.getTargetUsers());
                Set<Long> ids = pmUserDTOS.stream().map(NoticeSendDTO.User::getId).filter(t -> t != null).collect(Collectors.toSet());
                webSocketSendService.sendSiteMessage(dto.getCode(), dto.getParams(), ids,
                        Optional.ofNullable(dto.getFromUser()).map(NoticeSendDTO.User::getId).orElse(null), sendSetting);
            } else {
                LOGGER.info("sendsetting no opposite pm template,cann`t send pm");
            }
        } catch (CommonException e) {
            LOGGER.info("send station letter failed!", e);
        }
    }

    private void trySendEmail(NoticeSendDTO dto, SendSetting sendSetting, final boolean haveEmailTemplate) {
        //捕获异常,防止邮件发送失败，影响站内信发送
        try {
            if (haveEmailTemplate) {
                Set<String> emails = getNeedSendEmail(dto, sendSetting);
                emailSendService.sendEmail(dto.getCode(), dto.getParams(), emails, sendSetting);
            } else {
                LOGGER.info("sendsetting no opposite email template,cann`t send email");
            }
        } catch (CommonException e) {
            LOGGER.info("send email failed!", e);
        }
    }

    /**
     * 如果发送设置允许配置通知
     * 得到没有禁用接收邮件通知的用户
     * 否则得到全部用户
     *
     * @param dto
     * @param sendSetting
     * @param users
     * @return
     */
    private List<UserDTO> getEmailTargetUsers(final NoticeSendDTO dto, final SendSetting sendSetting, final List<UserDTO> users) {
        if (!sendSetting.getAllowConfig()) {
            return users;
        }
        return users.stream().filter(user -> {
            ReceiveSetting setting = new ReceiveSetting(sendSetting.getId(), MessageType.EMAIL.getValue(), dto.getSourceId(), sendSetting.getLevel(), user.getId());
            return receiveSettingMapper.selectCount(setting) == 0;
        }).collect(Collectors.toList());
    }

    /**
     * 如果发送设置允许配置通知
     * 得到没有禁用接收站内信通知的用户
     * 否则得到全部用户
     *
     * @param dto
     * @param sendSetting
     * @param users
     * @return
     */
    private List<NoticeSendDTO.User> getPmTargetUsers(final NoticeSendDTO dto, final SendSetting sendSetting, final List<NoticeSendDTO.User> users) {
        if (!sendSetting.getAllowConfig()) {
            return users;
        }
        return users.stream().filter(user -> {
            ReceiveSetting setting = new ReceiveSetting(sendSetting.getId(), MessageType.PM.getValue(), dto.getSourceId(), sendSetting.getLevel(), user.getId());
            return receiveSettingMapper.selectCount(setting) == 0;
        }).collect(Collectors.toList());
    }
}
