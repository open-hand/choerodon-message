package io.choerodon.message.app.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.message.api.vo.EmailConfigVO;
import io.choerodon.message.app.service.ConfigC7nService;
import io.choerodon.message.infra.enums.ConfigNameEnum;

import io.choerodon.message.infra.dto.iam.TenantDTO;

import org.hzero.message.app.service.EmailServerService;
import org.hzero.message.app.service.SmsServerService;
import org.hzero.message.domain.entity.EmailProperty;
import org.hzero.message.domain.entity.EmailServer;
import org.hzero.message.domain.entity.SmsServer;
import org.hzero.message.domain.repository.EmailServerRepository;
import org.hzero.message.infra.supporter.EmailSupporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private EmailServerRepository emailServerRepository;
    @Autowired
    private SmsServerService smsServerService;

    @Override
    public EmailConfigVO createOrUpdateEmail(EmailConfigVO emailConfigVO) {
        EmailServer emailServer = emailServerService.getEmailServer(TenantDTO.DEFAULT_TENANT_ID, ConfigNameEnum.EMAIL_NAME.value());
        // 处理ssl配置
        List<EmailProperty> emailProperties = emailServer.getEmailProperties();
        List<EmailProperty> tempProperties = new ArrayList<>(emailProperties);
        if (emailConfigVO.getSsl()) {
            if (!isSsl(emailProperties)) {
                List<EmailProperty> sslProperties = initEmailProperties(emailServer.getServerId(), emailConfigVO.getPort().toString());
                emailProperties.addAll(sslProperties);
            }
            emailServer.setEmailProperties(emailProperties);
        } else {
            if (isSsl(emailProperties)) {
                if (!CollectionUtils.isEmpty(tempProperties)) {
                    for (EmailProperty property : tempProperties) {
                        if (property.getPropertyCode().equals(SSL_PROPERTY_CLASS)
                                || property.getPropertyCode().equals(SSL_PROPERTY_PORT)
                                || property.getPropertyCode().equals(SSL_PROPERTY_ENABLE)) {
                            emailProperties.remove(property);
                        }
                    }
                }
            }
            emailServer.setEmailProperties(emailProperties);
        }
        EmailServer newEmailServer = setEmailServer(emailConfigVO, emailServer);
        newEmailServer.setServerCode(ConfigNameEnum.EMAIL_NAME.value());
        newEmailServer.setServerName(ConfigNameEnum.configNames.get(ConfigNameEnum.EMAIL_NAME.value()));
        newEmailServer.setTenantId(TenantDTO.DEFAULT_TENANT_ID);
        if (StringUtils.isEmpty(emailServer.getServerId())) {
            throw new CommonException("error.get.server.email");
        } else {
            emailServerService.updateEmailServer(TenantDTO.DEFAULT_TENANT_ID, newEmailServer);
        }
        return emailConfigVO;
    }

    @Override
    public EmailConfigVO selectEmail() {
        EmailServer emailServer = emailServerService.getEmailServer(TenantDTO.DEFAULT_TENANT_ID, ConfigNameEnum.EMAIL_NAME.value());
        if (emailServer == null) {
            return null;
        }
        emailServer.setObjectVersionNumber(emailServerRepository.selectByPrimaryKey(emailServer.getServerId()).getObjectVersionNumber());
        return setEmailConfigVO(new EmailConfigVO(), emailServer);
    }

    @Override
    public void testEmailConnect() {
        JavaMailSenderImpl javaMailSender = (JavaMailSenderImpl) EmailSupporter.javaMailSender(emailServerService.getEmailServer(TenantDTO.DEFAULT_TENANT_ID, ConfigNameEnum.EMAIL_NAME.value()));
        try {
            javaMailSender.testConnection();
        } catch (MessagingException e) {
            throw new CommonException("error.emailConfig.testConnectFailed", e);
        }
    }

    @Override
    public SmsServer createOrUpdateSmsServer(SmsServer smsServer) {
        SmsServer oleSmsServer = smsServerService.getSmsServer(TenantDTO.DEFAULT_TENANT_ID, ConfigNameEnum.SMS_NAME.value());
        smsServer.setTenantId(TenantDTO.DEFAULT_TENANT_ID);
        smsServer.setServerCode(ConfigNameEnum.SMS_NAME.value());
        smsServer.setServerName(ConfigNameEnum.configNames.get(ConfigNameEnum.SMS_NAME.value()));
        if (StringUtils.isEmpty(oleSmsServer.getServerId())) {
            throw new CommonException("error.get.server.sms");
        } else {
            smsServerService.updateSmsServer(TenantDTO.DEFAULT_TENANT_ID, smsServer);
        }
        return smsServer;
    }

    @Override
    public SmsServer selectSms() {
        return smsServerService.getSmsServer(TenantDTO.DEFAULT_TENANT_ID, ConfigNameEnum.SMS_NAME.value());
    }

    private List<EmailProperty> initEmailProperties(Long emailServerId, String port) {
        List<EmailProperty> propertyList = new ArrayList<>();

        EmailProperty emailPropertyClass = new EmailProperty();
        emailPropertyClass.setPropertyCode(SSL_PROPERTY_CLASS);
        emailPropertyClass.setPropertyValue(SSL_PROPERTY_CLASS_VALUE);
        emailPropertyClass.setTenantId(TenantDTO.DEFAULT_TENANT_ID);
        emailPropertyClass.setServerId(emailServerId);
        propertyList.add(emailPropertyClass);

        EmailProperty emailPropertyPort = new EmailProperty();
        emailPropertyPort.setPropertyCode(SSL_PROPERTY_PORT);
        emailPropertyPort.setPropertyValue(port);
        emailPropertyPort.setTenantId(TenantDTO.DEFAULT_TENANT_ID);
        emailPropertyPort.setServerId(emailServerId);
        propertyList.add(emailPropertyPort);

        EmailProperty emailPropertyEnable = new EmailProperty();
        emailPropertyEnable.setPropertyCode(SSL_PROPERTY_ENABLE);
        emailPropertyEnable.setPropertyValue(Boolean.TRUE.toString());
        emailPropertyEnable.setTenantId(TenantDTO.DEFAULT_TENANT_ID);
        emailPropertyEnable.setServerId(emailServerId);
        propertyList.add(emailPropertyEnable);
        return propertyList;
    }

    private EmailServer setEmailServer(EmailConfigVO emailConfigVO, EmailServer emailServer) {
        emailServer.setHost(emailConfigVO.getHost());
        emailServer.setPort(emailConfigVO.getPort().toString());
        emailServer.setProtocolMeaning(emailConfigVO.getProtocol());
        emailServer.setUsername(emailConfigVO.getAccount());
        emailServer.setSender(emailConfigVO.getSendName());
        emailServer.setPasswordEncrypted(emailConfigVO.getPassword());
        emailServer.setObjectVersionNumber(emailConfigVO.getObjectVersionNumber());
        return emailServer;
    }

    private EmailConfigVO setEmailConfigVO(EmailConfigVO emailConfigVO, EmailServer emailServer) {
        emailConfigVO.setHost(emailServer.getHost());
        emailConfigVO.setPort(Integer.parseInt(emailServer.getPort()));
        emailConfigVO.setProtocol(emailServer.getProtocol());
        emailConfigVO.setSendName(emailServer.getSender());
        emailConfigVO.setAccount(emailServer.getUsername());
        emailConfigVO.setPassword(emailServer.getPasswordEncrypted());
        emailConfigVO.setObjectVersionNumber(emailServer.getObjectVersionNumber());
        Map<String, String> map = emailServer.getEmailProperties().stream().collect(Collectors.toMap(EmailProperty::getPropertyCode, EmailProperty::getPropertyValue));
        if (!CollectionUtils.isEmpty(map) && map.containsKey(SSL_PROPERTY_ENABLE) && Boolean.parseBoolean(map.get(SSL_PROPERTY_ENABLE))) {
            emailConfigVO.setSsl(true);
        } else {
            emailConfigVO.setSsl(false);
        }
        return emailConfigVO;
    }

    private Boolean isSsl(List<EmailProperty> emailProperties) {
        boolean index = false;
        if (!CollectionUtils.isEmpty(emailProperties)) {
            for (EmailProperty t : emailProperties) {
                if (t.getPropertyCode().equals(SSL_PROPERTY_CLASS)
                        || t.getPropertyCode().equals(SSL_PROPERTY_PORT)
                        || t.getPropertyCode().equals(SSL_PROPERTY_ENABLE)) {
                    index = true;
                    break;
                }
            }
        }
        return index;
    }

}
