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
import org.springframework.util.StringUtils;

import java.util.Collections;
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
        Long[] userIds = dto.getTargetUsersIds().toArray(new Long[dto.getTargetUsersIds().size()]);
        List<UserDTO> userDTOS = userFeignClient.listUsersByIds(userIds).getBody();
        List<UserDTO> emailUserDTOS = getTargetUsers(dto, sendSetting, userDTOS, MessageType.EMAIL);
        Set<String> emails = emailUserDTOS.stream().map(UserDTO::getEmail).filter(t -> t != null).collect(Collectors.toSet());
        //捕获异常,防止邮件发送失败，影响站内信发送
        try {
            if (haveEmailTemplate) {
                emailSendService.sendEmail(dto.getCode(), dto.getParams(), emails, sendSetting);
            }
        } catch (CommonException e) {
            LOGGER.info("send email failed!", e);
        }
        List<UserDTO> pmUserDTOS = getTargetUsers(dto, sendSetting, userDTOS, MessageType.PM);
        Set<Long> ids = pmUserDTOS.stream().map(UserDTO::getId).filter(t -> t != null).collect(Collectors.toSet());
        try {
            if (havePmTemplate) {
                webSocketSendService.sendSiteMessage(dto.getCode(), dto.getParams(), ids, dto.getFromUserId(), sendSetting);
            }
        } catch (CommonException e) {
            LOGGER.info("send station letter failed!", e);
        }
    }

    /**
     * 如果发送设置允许配置通知
     * 得到没有禁用接收通知的用户
     * 否则得到全部用户
     *
     * @param dto
     * @param sendSetting
     * @param userDTOS
     * @param messageType
     * @return
     */
    private List<UserDTO> getTargetUsers(NoticeSendDTO dto, SendSetting sendSetting, List<UserDTO> userDTOS, MessageType messageType) {
        if (!sendSetting.getAllowConfig()) {
            return userDTOS;
        }
        if (dto.getSourceId() == null) {
            dto.setSourceId(0L);
        }
        return userDTOS.stream().filter(user -> {
            ReceiveSetting setting = new ReceiveSetting(sendSetting.getId(), messageType.getValue(), dto.getSourceId(), sendSetting.getLevel(), user.getId());
            return receiveSettingMapper.selectCount(setting) == 0;
        }).collect(Collectors.toList());
    }
}
