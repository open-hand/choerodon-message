package io.choerodon.notify.api.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.notify.api.dto.EmailConfigDTO;
import io.choerodon.notify.api.dto.NoticeSendDTO;
import io.choerodon.notify.api.pojo.MessageType;
import io.choerodon.notify.api.service.EmailSendService;
import io.choerodon.notify.api.service.NoticesSendService;
import io.choerodon.notify.api.service.WebSocketSendService;
import io.choerodon.notify.domain.ReceiveSetting;
import io.choerodon.notify.domain.SendSetting;
import io.choerodon.notify.infra.mapper.ReceiveSettingMapper;
import io.choerodon.notify.infra.mapper.SendSettingMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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

    public NoticesSendServiceImpl(EmailSendService emailSendService,
                                  @Qualifier("pmWsSendService") WebSocketSendService webSocketSendService,
                                  ReceiveSettingMapper receiveSettingMapper,
                                  SendSettingMapper sendSettingMapper) {
        this.emailSendService = emailSendService;
        this.webSocketSendService = webSocketSendService;
        this.receiveSettingMapper = receiveSettingMapper;
        this.sendSettingMapper = sendSettingMapper;
    }

    @Override
    public void testEmailConnect(EmailConfigDTO config) {
        emailSendService.testEmailConnect(config);
    }

    @Override
    public void sendNotice(NoticeSendDTO dto) {
        List<NoticeSendDTO.User> users = getTargetUsers(dto, MessageType.EMAIL);
        Set<String> emails = users.stream().map(NoticeSendDTO.User::getEmail).collect(Collectors.toSet());
        //捕获异常,防止邮件发送失败，影响站内信发送
        try {
            emailSendService.sendEmail(dto.getCode(), dto.getParams(), emails);
        } catch (CommonException e) {
            LOGGER.info("send email failed!", e);
        }

        Set<Long> ids =getTargetUsers(dto, MessageType.PM).stream().map(NoticeSendDTO.User::getId).collect(Collectors.toSet());
        try {
            webSocketSendService.sendSiteMessage(dto.getCode(), dto.getParams(), ids,
                    Optional.ofNullable(dto.getFromUser()).map(NoticeSendDTO.User::getId).orElse(null));
        } catch (CommonException e) {
            LOGGER.info("send station letter failed!", e);
        }
    }

    /**
     * 得到没有禁用接收通知的用户
     *
     * @param dto
     * @param messageType 指定通知类型
     * @return
     */
    private List<NoticeSendDTO.User> getTargetUsers(NoticeSendDTO dto, MessageType messageType) {
        SendSetting sendSetting = sendSettingMapper.selectOne(new SendSetting(dto.getCode()));
        if (dto.getCode() == null || sendSetting == null) {
            LOGGER.info("no sendsetting,cann`t send email.");
            return Collections.emptyList();
        }
        if (dto.getSourceId() == null) {
            dto.setSourceId(0L);
        }
        return dto.getTargetUsers().stream().filter(user -> {
            ReceiveSetting setting = new ReceiveSetting(sendSetting.getId(), messageType.getValue(), dto.getSourceId(), sendSetting.getLevel(), user.getId());
            return receiveSettingMapper.selectCount(setting) == 0;
        }).collect(Collectors.toList());
    }
}
