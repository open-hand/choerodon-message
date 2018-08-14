package io.choerodon.notify.api.service.impl;

import freemarker.template.TemplateException;
import io.choerodon.core.exception.FeignException;
import io.choerodon.notify.api.dto.EmailSendDTO;
import io.choerodon.notify.api.service.NoticesSendService;
import io.choerodon.notify.domain.Config;
import io.choerodon.notify.domain.SendSetting;
import io.choerodon.notify.domain.Template;
import io.choerodon.notify.infra.mapper.ConfigMapper;
import io.choerodon.notify.infra.mapper.SendSettingMapper;
import io.choerodon.notify.infra.mapper.TemplateMapper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

@Service
public class NoticesSendServiceImpl implements NoticesSendService {

    private final SendSettingMapper sendSettingMapper;

    private final TemplateMapper templateMapper;

    private final ConfigMapper configMapper;

    private static final String ENCODE_UTF8 = "utf-8";

    private final FreeMarkerConfigBuilder freeMarkerConfigBuilder;

    public NoticesSendServiceImpl(SendSettingMapper sendSettingMapper,
                                  ConfigMapper configMapper,
                                  TemplateMapper templateMapper,
                                  FreeMarkerConfigBuilder freeMarkerConfigBuilder) {
        this.sendSettingMapper = sendSettingMapper;
        this.configMapper = configMapper;
        this.templateMapper = templateMapper;
        this.freeMarkerConfigBuilder = freeMarkerConfigBuilder;
    }

    @Override
    public void postEmail(EmailSendDTO dto) {
        Config config = configMapper.selectOne(new Config());
        if (config == null || StringUtils.isEmpty(config.getEmailAccount())) {
            throw new FeignException("error.noticeSend.emailConfigNotSet");
        }
        SendSetting sendSetting = sendSettingMapper.selectOne(new SendSetting(dto.getCode()));
        if (dto.getCode() == null || sendSetting == null) {
            throw new FeignException("error.noticeSend.codeNotFound");
        }
        if (sendSetting.getEmailTemplateId() == null) {
            throw new FeignException("error.noticeSend.emailTemplateNotSet");
        }
        io.choerodon.notify.domain.Template template = templateMapper.selectByPrimaryKey(sendSetting.getEmailTemplateId());
        if (template == null) {
            throw new FeignException("error.noticeSend.emailTemplateNotSet");
        }
        JavaMailSender mailSender = createMailSender(config);
        sendEmail(config, dto, template, mailSender);
    }

    @Override
    public void testEmailConnect() {
        Config config = configMapper.selectOne(new Config());
        if (config == null || StringUtils.isEmpty(config.getEmailAccount())) {
            throw new FeignException("error.noticeSend.emailConfigNotSet");
        }
        JavaMailSenderImpl mailSender = createMailSender(config);
        try {
            mailSender.testConnection();
        } catch (MessagingException e) {
            throw new FeignException("error.emailConfig.testConnectFailed");
        }
    }

    private JavaMailSenderImpl createMailSender(final Config config) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(config.getEmailHost());
        mailSender.setPort(config.getEmailPort());
        mailSender.setUsername(config.getEmailAccount());
        mailSender.setPassword(config.getEmailPassword());
        mailSender.setProtocol(config.getEmailProtocol());
        Properties properties = new Properties();
        if (Config.EMAIL_PROTOCOL_SMTP.equals(config.getEmailProtocol()) && config.getEmailSsl()) {
            properties.put(Config.EMAIL_SSL_SMTP, true);
        }
        if (Config.EMAIL_PROTOCOL_IMAP.equals(config.getEmailProtocol()) && config.getEmailSsl()) {
            properties.put(Config.EMAIL_SSL_IMAP, true);
        }
        if (Config.EMAIL_PROTOCOL_POP3.equals(config.getEmailProtocol()) && config.getEmailSsl()) {
            properties.put(Config.EMAIL_PROTOCOL_POP3, true);
        }
        mailSender.setJavaMailProperties(properties);
        return mailSender;
    }

    private void sendEmail(final Config config, final EmailSendDTO dto,
                           final io.choerodon.notify.domain.Template template,
                           final JavaMailSender mailSender) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, ENCODE_UTF8);
            helper.setFrom(new InternetAddress("\"" + MimeUtility.encodeText(config.getEmailSendName()) + "\"<" + config.getEmailAccount() + ">"));
            helper.setTo(dto.getEmailAddress());
            helper.setSubject(MimeUtility.encodeText(template.getEmailTitle(), ENCODE_UTF8, "B"));

            helper.setText(renderStringTemplate(template, dto.getVariables()), true);
            mailSender.send(msg);
        } catch (Exception e) {
            throw new FeignException("error.noticeSend.emailSendError", e);
        }

    }


    public final String renderStringTemplate(final Template template, final Map<String, Object> variables) throws IOException, TemplateException{
        freemarker.template.Template ft = freeMarkerConfigBuilder.getTemplate(template.getCode());
        if (ft == null) {
            ft = freeMarkerConfigBuilder.addTemplate(template.getCode(), template.getEmailContent());
        }
        if (ft == null) {
            throw new FeignException("error.noticeSend.emailParseError");
        }
        return FreeMarkerTemplateUtils.processTemplateIntoString(ft, variables);
    }


}
