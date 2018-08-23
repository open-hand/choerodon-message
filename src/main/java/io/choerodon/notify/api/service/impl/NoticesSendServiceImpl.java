package io.choerodon.notify.api.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.TemplateException;
import io.choerodon.core.exception.CommonException;
import io.choerodon.notify.api.dto.EmailConfigDTO;
import io.choerodon.notify.api.dto.EmailSendDTO;
import io.choerodon.notify.api.exception.EmailSendException;
import io.choerodon.notify.api.pojo.EmailSendError;
import io.choerodon.notify.api.service.NoticesSendService;
import io.choerodon.notify.domain.*;
import io.choerodon.notify.infra.cache.ConfigCache;
import io.choerodon.notify.infra.mapper.RecordMapper;
import io.choerodon.notify.infra.mapper.SendSettingMapper;
import io.choerodon.notify.infra.mapper.TemplateMapper;
import io.choerodon.notify.infra.utils.ConvertUtils;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import rx.Observable;
import rx.schedulers.Schedulers;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class NoticesSendServiceImpl implements NoticesSendService {

    private final SendSettingMapper sendSettingMapper;

    private final TemplateMapper templateMapper;

    private final ConfigCache configCache;

    private static final String ENCODE_UTF8 = "utf-8";

    private final FreeMarkerConfigBuilder freeMarkerConfigBuilder;

    private final ModelMapper modelMapper = new ModelMapper();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final RecordMapper recordMapper;

    private final EmailQueueObservable emailQueueObservable;

    private final Executor executor;

    public NoticesSendServiceImpl(SendSettingMapper sendSettingMapper,
                                  TemplateMapper templateMapper,
                                  FreeMarkerConfigBuilder freeMarkerConfigBuilder,
                                  ConfigCache configCache,
                                  RecordMapper recordMapper,
                                  EmailQueueObservable emailQueueObservable,
                                  @Qualifier("asyncSendNoticeExecutor") Executor executor) {
        this.sendSettingMapper = sendSettingMapper;
        this.templateMapper = templateMapper;
        this.freeMarkerConfigBuilder = freeMarkerConfigBuilder;
        this.configCache = configCache;
        this.recordMapper = recordMapper;
        this.emailQueueObservable = emailQueueObservable;
        this.executor = executor;
    }

    @Override
    public void createMailSenderAndSendEmail(EmailSendDTO dto) {
        SendSetting sendSetting = sendSettingMapper.selectOne(new SendSetting(dto.getCode()));
        if (dto.getCode() == null || sendSetting == null) {
            throw new CommonException("error.noticeSend.codeNotFound");
        }
        if (sendSetting.getEmailTemplateId() == null) {
            throw new CommonException("error.noticeSend.emailTemplateNotSet");
        }
        io.choerodon.notify.domain.Template template = templateMapper.selectByPrimaryKey(sendSetting.getEmailTemplateId());
        if (template == null) {
            throw new CommonException("error.emailTemplate.notExist");
        }
        final Config config = configCache.getEmailConfig();
        Record record = new Record(sendSetting, MessageType.EMAIL.getValue());
        record.setReceiveAccount(dto.getDestinationEmail());
        record.setTemplateType(template.getBusinessType());
        record.setTemplateId(template.getId());
        record.setTemplate(template);
        record.setVariablesMap(dto.getVariables());
        record.setConfig(config);
        record.setVariables(ConvertUtils.convertMapToJson(objectMapper, dto.getVariables()));
        record.setMailSender(createEmailSender(config));
        if (recordMapper.insert(record) != 1) {
            throw new CommonException("error.noticeSend.recordInsert");
        }
        if (sendSetting.getIsSendInstantly()) {
            sendEmail(record, true);
        } else {
            emailQueueObservable.emit(record);
        }
    }

    @Override
    public void testEmailConnect(EmailConfigDTO dto) {
        Config config = modelMapper.map(dto, Config.class);
        JavaMailSenderImpl mailSender = createEmailSender(config);
        try {
            mailSender.testConnection();
        } catch (MessagingException e) {
            throw new CommonException("error.emailConfig.testConnectFailed", e);
        }
    }

    @Override
    public void sendEmail(final Record record, boolean retry) {
        try {
            doSendAndUpdateRecord(record);
        } catch (EmailSendException e) {
            recordMapper.updateRecordStatus(record.getId(), Record.RecordStatus.FAILED.getValue(),
                    e.getError().getReason());
            if (retry && record.getMaxRetryCount() > 0) {
                retrySend(record);
            }
            throw new CommonException("error.noticeSend.email", e);
        } catch (Exception e) {
            recordMapper.updateRecordStatus(record.getId(), Record.RecordStatus.FAILED.getValue(),
                    EmailSendError.UNKNOWN_ERROR.getReason());
            if (retry && record.getMaxRetryCount() > 0) {
                retrySend(record);
            }
            throw new CommonException("error.noticeSend.email", e);
        }

    }

    private void retrySend(final Record record) {
        Observable.just(record)
                .map(t -> {
                    doSendAndUpdateRecord(record);
                    return t;
                })
                .retryWhen(x -> x.zipWith(Observable.range(1, record.getMaxRetryCount()),
                        (e, retryCount) -> {
                            log.info("retry send email, retryCount {}, Record {}", retryCount, record);
                            if (retryCount >= record.getMaxRetryCount()) {
                                log.warn("error.emailSend.retrySendError {}", e.toString());
                                if (e instanceof EmailSendException) {
                                    recordMapper.updateRecordStatus(record.getId(), Record.RecordStatus.FAILED.getValue(),
                                            ((EmailSendException) e).getError().getReason());
                                } else {
                                    recordMapper.updateRecordStatus(record.getId(), Record.RecordStatus.FAILED.getValue(),
                                            EmailSendError.UNKNOWN_ERROR.getReason());
                                }
                            }
                            return retryCount;
                        }).flatMap(y -> Observable.timer(1, TimeUnit.SECONDS)))
                .subscribeOn(Schedulers.from(executor))
                .subscribe((Record rc) -> {
                });
    }

    private void doSendAndUpdateRecord(final Record record) {
        try {
            final JavaMailSenderImpl mailSender = record.getMailSender();
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, ENCODE_UTF8);
            helper.setFrom(new InternetAddress("\"" + MimeUtility.encodeText(record.getConfig().getEmailSendName())
                    + "\"<" + record.getConfig().getEmailAccount() + ">"));
            helper.setTo(record.getReceiveAccount());
            helper.setSubject(MimeUtility.encodeText(record.getTemplate().getEmailTitle(), ENCODE_UTF8, "B"));
            helper.setText(renderStringTemplate(record.getTemplate(), record.getVariablesMap()), true);
            mailSender.send(msg);
            recordMapper.updateRecordStatus(record.getId(), Record.RecordStatus.COMPLETE.getValue(), null);
        } catch (MailAuthenticationException e) {
            throw new EmailSendException(e, EmailSendError.AUTH_ERROR);
        } catch (MessagingException e) {
            throw new EmailSendException(e, EmailSendError.MIME_ERROR);
        } catch (TemplateException | IOException e) {
            throw new EmailSendException(e, EmailSendError.TEMPLATE_ERROR);
        } catch (MailSendException e) {
            if (e.getMessage().contains("Mail server connection failed")) {
                throw new EmailSendException(e, EmailSendError.ADDRESS_ERROR);
            }
            throw new EmailSendException(e, EmailSendError.NETWORK_ERROR);
        } catch (Exception e) {
            throw new EmailSendException(e, EmailSendError.UNKNOWN_ERROR);
        }
    }

    @Override
    public JavaMailSenderImpl createEmailSender(final Config config) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(config.getEmailHost());
        mailSender.setPort(config.getEmailPort());
        mailSender.setUsername(config.getEmailAccount());
        mailSender.setPassword(config.getEmailPassword());
        mailSender.setProtocol(config.getEmailProtocol().toLowerCase());
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        if (Config.EMAIL_PROTOCOL_SMTP.equalsIgnoreCase(config.getEmailProtocol()) && config.getEmailSsl()) {
            properties.put(Config.EMAIL_SSL_SMTP, true);
            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.put("mail.smtp.port", config.getEmailPort());
        }

        mailSender.setJavaMailProperties(properties);
        return mailSender;
    }

    private String renderStringTemplate(final Template template, final Map<String, Object> variables) throws IOException, TemplateException {
        freemarker.template.Template ft = freeMarkerConfigBuilder.getTemplate(template.getCode());
        if (ft == null) {
            ft = freeMarkerConfigBuilder.addTemplate(template.getCode(), template.getEmailContent());
        }
        if (ft == null) {
            throw new CommonException("error.noticeSend.emailParseError");
        }
        return FreeMarkerTemplateUtils.processTemplateIntoString(ft, variables);
    }


}
