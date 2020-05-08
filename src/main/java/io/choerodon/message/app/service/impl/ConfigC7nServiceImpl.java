package io.choerodon.message.app.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.hzero.core.util.Results;
import org.hzero.message.app.service.EmailServerService;
import org.hzero.message.app.service.SmsServerService;
import org.hzero.message.domain.entity.EmailProperty;
import org.hzero.message.domain.entity.EmailServer;
import org.hzero.message.domain.entity.SmsServer;
import org.hzero.message.domain.repository.EmailServerRepository;
import org.hzero.message.infra.supporter.EmailSupporter;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import io.choerodon.core.exception.CommonException;
import io.choerodon.message.api.vo.EmailConfigVO;
import io.choerodon.message.app.service.ConfigC7nService;
import io.choerodon.message.infra.ConfigNameEnum;

/**
 * @author scp
 * @date 2020/4/28
 * @description
 */
@Service
public class ConfigC7nServiceImpl implements ConfigC7nService {

    private static final String SSL_PROPERTY_CLASS = "mail.smtp.socketFactory.class";
    private static final String SSL_PROPERTY_CLASS_VALUE = "javax.net.ssl.SSLSocketFactory";
    private static final String SSL_PROPERTY_PORT = "mail.smtp.socketFactory.port";
    private static final String SSL_PROPERTY_ENABLE = "mail.smtp.ssl.enable";

    @Autowired
    private EmailServerService emailServerService;
    @Autowired
    private SmsServerService smsServerService;

    @Override
    public EmailConfigVO createOrUpdateEmail(EmailConfigVO emailConfigVO) {
        EmailServer emailServer = emailServerService.getEmailServer(0L, ConfigNameEnum.EMAIL_NAME.value());
        if (emailConfigVO.getSsl() && emailServer.getEmailProperties() == null) {
            emailServer.setEmailProperties(initEmailProperties(emailServer.getServerId(), emailConfigVO.getPort().toString()));
        }

        EmailServer newEmailServer = setEmailServer(emailConfigVO, emailServer);
        newEmailServer.setServerCode(ConfigNameEnum.EMAIL_NAME.value());
        newEmailServer.setServerName(ConfigNameEnum.configNames.get(ConfigNameEnum.EMAIL_NAME.value()));
        newEmailServer.setTenantId(0L);
        newEmailServer.setTryTimes(0);
        if (StringUtils.isEmpty(emailServer.getServerId())) {
            emailServerService.createEmailServer(newEmailServer);
        } else {
            emailServerService.updateEmailServer(newEmailServer);
        }
        return emailConfigVO;
    }

    @Override
    public EmailConfigVO selectEmail() {
        EmailServer emailServer = emailServerService.getEmailServer(0L, ConfigNameEnum.EMAIL_NAME.value());
        return setEmailConfigVO(new EmailConfigVO(), emailServer);
    }

    @Override
    public void testEmailConnect() {
        JavaMailSenderImpl javaMailSender = (JavaMailSenderImpl) EmailSupporter.javaMailSender(emailServerService.getEmailServer(0L, ConfigNameEnum.EMAIL_NAME.value()));
        try {
            javaMailSender.testConnection();
        } catch (MessagingException e) {
            throw new CommonException("error.emailConfig.testConnectFailed", e);
        }
    }

    @Override
    public SmsServer createOrUpdateSmsServer(SmsServer smsServer) {
        SmsServer oleSmsServer = smsServerService.getSmsServer(0L, ConfigNameEnum.SMS_NAME.value());
        smsServer.setTenantId(0L);
        smsServer.setServerCode(ConfigNameEnum.SMS_NAME.value());
        smsServer.setServerName(ConfigNameEnum.configNames.get(ConfigNameEnum.SMS_NAME.value()));
        if (StringUtils.isEmpty(oleSmsServer.getServerId())) {
            smsServerService.createSmsServer(smsServer);
        } else {
            smsServerService.updateSmsServer(smsServer);
        }
        return smsServer;
    }

    @Override
    public SmsServer selectSms() {
        return smsServerService.getSmsServer(0L, ConfigNameEnum.SMS_NAME.value());
    }

    private List<EmailProperty> initEmailProperties(Long emailServerId, String port) {
        List<EmailProperty> propertyList = new ArrayList<>();

        EmailProperty emailPropertyClass = new EmailProperty();
        emailPropertyClass.setPropertyCode(SSL_PROPERTY_CLASS);
        emailPropertyClass.setPropertyValue(SSL_PROPERTY_CLASS_VALUE);
        emailPropertyClass.setTenantId(0L);
        emailPropertyClass.setServerId(emailServerId);
        propertyList.add(emailPropertyClass);

        EmailProperty emailPropertyPort = new EmailProperty();
        emailPropertyPort.setPropertyCode(SSL_PROPERTY_PORT);
        emailPropertyPort.setPropertyValue(port);
        emailPropertyPort.setTenantId(0L);
        emailPropertyPort.setServerId(emailServerId);
        propertyList.add(emailPropertyPort);

        EmailProperty emailPropertyEnable = new EmailProperty();
        emailPropertyEnable.setPropertyCode(SSL_PROPERTY_ENABLE);
        emailPropertyEnable.setPropertyValue(Boolean.TRUE.toString());
        emailPropertyEnable.setTenantId(0L);
        emailPropertyEnable.setServerId(emailServerId);
        propertyList.add(emailPropertyEnable);
        return propertyList;
    }

    private EmailServer setEmailServer(EmailConfigVO emailConfigVO, EmailServer emailServer) {
        emailServer.setHost(emailConfigVO.getHost());
        emailServer.setPort(emailConfigVO.getPort().toString());
        emailServer.setProtocolMeaning(emailConfigVO.getProtocol());
        emailServer.setUsername(emailConfigVO.getSendName());
        emailServer.setSender(emailConfigVO.getAccount());
        emailServer.setPasswordEncrypted(emailConfigVO.getPassword());
        emailServer.setObjectVersionNumber(emailConfigVO.getObjectVersionNumber());
        return emailServer;
    }

    private EmailConfigVO setEmailConfigVO(EmailConfigVO emailConfigVO, EmailServer emailServer) {
        emailConfigVO.setHost(emailServer.getHost());
        emailConfigVO.setPort(Integer.parseInt(emailServer.getPort()));
        emailConfigVO.setProtocol(emailServer.getProtocolMeaning());
        emailConfigVO.setSendName(emailServer.getUsername());
        emailConfigVO.setAccount(emailServer.getSender());
        emailConfigVO.setPassword(emailServer.getPasswordEncrypted());
        emailConfigVO.setObjectVersionNumber(emailConfigVO.getObjectVersionNumber());
        return emailConfigVO;
    }
}
