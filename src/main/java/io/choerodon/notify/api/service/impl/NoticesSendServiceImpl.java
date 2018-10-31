package io.choerodon.notify.api.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.notify.api.dto.EmailConfigDTO;
import io.choerodon.notify.api.dto.EmailSendDTO;
import io.choerodon.notify.api.dto.NoticeSendDTO;
import io.choerodon.notify.api.dto.WsSendDTO;
import io.choerodon.notify.api.service.EmailSendService;
import io.choerodon.notify.api.service.NoticesSendService;
import io.choerodon.notify.api.service.WebSocketSendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NoticesSendServiceImpl implements NoticesSendService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NoticesSendServiceImpl.class);

    private EmailSendService emailSendService;

    private WebSocketSendService webSocketSendService;

    public NoticesSendServiceImpl(EmailSendService emailSendService,
                                  @Qualifier("pmWsSendService") WebSocketSendService webSocketSendService) {
        this.emailSendService = emailSendService;
        this.webSocketSendService = webSocketSendService;
    }

    @Override
    public void sendEmail(EmailSendDTO dto) {
        emailSendService.sendEmail(dto.getCode(), dto.getVariables(), Collections.singleton(dto.getDestinationEmail()));
    }

    @Override
    public void sendSiteMessage(WsSendDTO dto) {
        webSocketSendService.sendSiteMessage(dto.getCode(), dto.getParams(), Collections.singleton(dto.getId()), null);
    }

    @Override
    public void testEmailConnect(EmailConfigDTO config) {
        emailSendService.testEmailConnect(config);
    }

    @Override
    public void sendNotice(NoticeSendDTO dto) {
        Set<String> emails = dto.getTargetUsers().stream().map(NoticeSendDTO.User::getEmail).collect(Collectors.toSet());
        //捕获异常,防止邮件发送失败，影响站内信发送
        try {
            emailSendService.sendEmail(dto.getCode(), dto.getParams(), emails);
        } catch (CommonException e) {
            LOGGER.info("send email failed!", e);
        }
        Set<Long> ids = dto.getTargetUsers().stream().map(NoticeSendDTO.User::getId).collect(Collectors.toSet());
        try {
            webSocketSendService.sendSiteMessage(dto.getCode(), dto.getParams(), ids,
                    Optional.ofNullable(dto.getFromUser()).map(NoticeSendDTO.User::getId).orElse(null));
        }catch (CommonException e){
            LOGGER.info("send station letter failed!", e);
        }
    }
}
