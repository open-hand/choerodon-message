package io.choerodon.notify.api.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.TemplateException;
import io.choerodon.core.exception.CommonException;
import io.choerodon.notify.api.dto.EmailConfigDTO;
import io.choerodon.notify.api.dto.EmailSendDTO;
import io.choerodon.notify.api.exception.EmailSendException;
import io.choerodon.notify.api.pojo.EmailSendError;
import io.choerodon.notify.api.pojo.MessageType;
import io.choerodon.notify.api.pojo.RecordSendData;
import io.choerodon.notify.api.pojo.RecordStatus;
import io.choerodon.notify.api.service.EmailSendService;
import io.choerodon.notify.api.service.NoticesSendService;
import io.choerodon.notify.domain.Config;
import io.choerodon.notify.domain.Record;
import io.choerodon.notify.domain.SendSetting;
import io.choerodon.notify.infra.cache.ConfigCache;
import io.choerodon.notify.infra.mapper.RecordMapper;
import io.choerodon.notify.infra.mapper.SendSettingMapper;
import io.choerodon.notify.infra.mapper.TemplateMapper;
import io.choerodon.notify.infra.utils.ConvertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import rx.Observable;
import rx.schedulers.Schedulers;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Service
public class EmailSendServiceImpl implements EmailSendService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoticesSendService.class);

    private final SendSettingMapper sendSettingMapper;

    private final TemplateMapper templateMapper;

    private final ConfigCache configCache;

    private static final String ENCODE_UTF8 = "utf-8";

    private final TemplateRender templateRender;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final RecordMapper recordMapper;

    private final EmailQueueObservable emailQueueObservable;

    private final Executor executor;


    public EmailSendServiceImpl(SendSettingMapper sendSettingMapper,
                                TemplateMapper templateMapper,
                                ConfigCache configCache,
                                RecordMapper recordMapper,
                                EmailQueueObservable emailQueueObservable,
                                TemplateRender templateRender,
                                @Qualifier("asyncSendNoticeExecutor") Executor executor) {
        this.sendSettingMapper = sendSettingMapper;
        this.templateMapper = templateMapper;
        this.configCache = configCache;
        this.recordMapper = recordMapper;
        this.emailQueueObservable = emailQueueObservable;
        this.templateRender = templateRender;
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
        Record record = new Record();
        record.setStatus(null);
        record.setRetryCount(0);
        record.setMessageType(MessageType.EMAIL.getValue());
        record.setReceiveAccount(dto.getDestinationEmail());
        record.setBusinessType(dto.getCode());
        record.setTemplateId(template.getId());
        record.setVariables(ConvertUtils.convertMapToJson(objectMapper, dto.getVariables()));
        record.setSendData(new RecordSendData(template, dto.getVariables(), createEmailSender(), sendSetting.getRetryCount()));
        if (recordMapper.insert(record) != 1) {
            throw new CommonException("error.noticeSend.recordInsert");
        }
        if (sendSetting.getIsSendInstantly()) {
            sendEmail(record, false);
        } else {
            emailQueueObservable.emit(record);
        }
    }

    @Override
    public void testEmailConnect(EmailConfigDTO dto) {
        JavaMailSenderImpl mailSender = createEmailSender();
        try {
            mailSender.testConnection();
        } catch (MessagingException e) {
            throw new CommonException("error.emailConfig.testConnectFailed", e);
        }
    }

    @Override
    public void sendEmail(final Record record, final boolean isManualRetry) {
        try {
            doSendAndUpdateRecord(record, isManualRetry);
        } catch (EmailSendException e) {
            sendFailedHandler(record, isManualRetry, e.getError().getReason(), e);
        } catch (Exception e) {
            sendFailedHandler(record, isManualRetry, EmailSendError.UNKNOWN_ERROR.getReason(), e);
        }
    }

    private void sendFailedHandler(final Record record, final boolean isManualRetry,
                                   final String reason, final Exception e) {
        boolean increase = false;
        if (isManualRetry) {
            increase = true;
        } else {
            if (record.getSendData().getMaxRetryCount() > 0) {
                retrySend(record);
            }
        }
        recordMapper.updateRecordStatusAndIncreaseCount(record.getId(), RecordStatus.FAILED.getValue(),
                reason, increase, null);
        throw new CommonException("error.noticeSend.email", e);
    }

    private void retrySend(final Record record) {
        Observable.just(record)
                .map(t -> {
                    doSendAndUpdateRecord(record, false);
                    return t;
                })
                .retryWhen(x -> x.zipWith(Observable.range(1, record.getSendData().getMaxRetryCount()),
                        (e, retryCount) -> {
                            LOGGER.info("retry send email, retryCount {}, Record {}", retryCount, record);
                            if (retryCount >= record.getSendData().getMaxRetryCount()) {
                                LOGGER.warn("error.emailSend.retrySendError {}", e);
                                if (e instanceof EmailSendException) {
                                    recordMapper.updateRecordStatusAndIncreaseCount(record.getId(), RecordStatus.FAILED.getValue(),
                                            ((EmailSendException) e).getError().getReason(), false, null);
                                } else {
                                    recordMapper.updateRecordStatusAndIncreaseCount(record.getId(), RecordStatus.FAILED.getValue(),
                                            EmailSendError.UNKNOWN_ERROR.getReason(), false, null);
                                }
                            }
                            return retryCount;
                        }).flatMap(y -> Observable.timer(1, TimeUnit.SECONDS)))
                .subscribeOn(Schedulers.from(executor))
                .subscribe((Record rc) -> {
                });
    }

    private void doSendAndUpdateRecord(final Record record, final boolean isManualRetry) {
        try {
            final JavaMailSenderImpl mailSender = record.getSendData().getMailSender();
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, ENCODE_UTF8);
            helper.setFrom(new InternetAddress("\"" + MimeUtility.encodeText(configCache.getEmailConfig().getEmailSendName())
                    + "\"<" + configCache.getEmailConfig().getEmailAccount() + ">"));
            helper.setTo(record.getReceiveAccount());
            helper.setSubject(MimeUtility.encodeText(record.getSendData().getTemplate().getEmailTitle(), ENCODE_UTF8, "B"));
            helper.setText(templateRender.renderTemplate(record.getSendData().getTemplate(), record.getSendData().getVariables()), true);
            mailSender.send(msg);
            if (isManualRetry) {
                recordMapper.updateRecordStatusAndIncreaseCount(record.getId(), RecordStatus.COMPLETE.getValue(), null, true, new Date());
            } else {
                recordMapper.updateRecordStatusAndIncreaseCount(record.getId(), RecordStatus.COMPLETE.getValue(), null, false, null);
            }

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
    public JavaMailSenderImpl createEmailSender() {
        final Config config = configCache.getEmailConfig();
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
            properties.put("mail.smtp.ssl.checkserveridentity", true);
            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.put("mail.smtp.port", config.getEmailPort());
        }

        mailSender.setJavaMailProperties(properties);
        return mailSender;
    }


}
