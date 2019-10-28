package io.choerodon.notify.api.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.TemplateException;
import io.choerodon.core.exception.CommonException;
import io.choerodon.notify.api.dto.EmailConfigDTO;
import io.choerodon.notify.api.dto.UserDTO;
import io.choerodon.notify.api.exception.EmailSendException;
import io.choerodon.notify.api.pojo.DefaultAutowiredField;
import io.choerodon.notify.api.pojo.EmailSendError;
import io.choerodon.notify.api.pojo.RecordSendData;
import io.choerodon.notify.api.pojo.RecordStatus;
import io.choerodon.notify.api.service.EmailSendService;
import io.choerodon.notify.api.service.NoticesSendService;
import io.choerodon.notify.domain.Record;
import io.choerodon.notify.infra.cache.ConfigCache;
import io.choerodon.notify.infra.dto.Config;
import io.choerodon.notify.infra.dto.SendSettingDTO;
import io.choerodon.notify.infra.dto.Template;
import io.choerodon.notify.infra.enums.SendingTypeEnum;
import io.choerodon.notify.infra.mapper.MailingRecordMapper;
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
import org.springframework.util.StringUtils;
import rx.Observable;
import rx.schedulers.Schedulers;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Service
public class EmailSendServiceImpl implements EmailSendService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoticesSendService.class);

    private final TemplateMapper templateMapper;

    private final ConfigCache configCache;

    private static final String ENCODE_UTF8 = "utf-8";

    private final TemplateRender templateRender;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final MailingRecordMapper mailingRecordMapper;

    private final EmailQueueObservable emailQueueObservable;

    private final Executor executor;


    public EmailSendServiceImpl(TemplateMapper templateMapper,
                                ConfigCache configCache,
                                MailingRecordMapper mailingRecordMapper,
                                EmailQueueObservable emailQueueObservable,
                                TemplateRender templateRender,
                                @Qualifier("asyncSendNoticeExecutor") Executor executor) {
        this.templateMapper = templateMapper;
        this.configCache = configCache;
        this.mailingRecordMapper = mailingRecordMapper;
        this.emailQueueObservable = emailQueueObservable;
        this.templateRender = templateRender;
        this.executor = executor;
    }


    @Override
    public void sendEmail(String code, Map<String, Object> params, Set<UserDTO> targetUsers, SendSettingDTO sendSetting) {
        LOGGER.trace("SendEmail code:{} to users: {}", code, targetUsers);
        Template template = templateMapper.selectOne(new Template().setSendingType(SendingTypeEnum.EMAIL.getValue()).setSendSettingCode(sendSetting.getCode()));
        validatorEmailTemplate(template);
        targetUsers.forEach(user -> {
            Record record = new Record();
            record.setStatus(null);
            record.setRetryCount(0);
            record.setReceiveAccount(user.getEmail());
            record.setSendSettingCode(sendSetting.getCode());
            record.setTemplateId(template.getId());
            Map<String, Object> newParams = DefaultAutowiredField.autowiredDefaultParams(params, user);
            record.setVariables(ConvertUtils.convertMapToJson(objectMapper, newParams));
            record.setSendData(new RecordSendData(template, newParams, createEmailSender(), sendSetting.getRetryCount()));
            if (mailingRecordMapper.insert(record) != 1) {
                throw new CommonException("error.noticeSend.recordInsert");
            }
            if (sendSetting.getIsSendInstantly()) {
                sendRecord(record, false);
            } else {
                emailQueueObservable.emit(record);
            }
        });
    }

    private void validatorEmailTemplate(Template template) {
        if (template == null) {
            throw new CommonException("error.emailTemplate.notExist");
        }
        if (StringUtils.isEmpty(template.getContent()) || StringUtils.isEmpty(template.getTitle())) {
            throw new CommonException("error.emailTemplate.notValid");
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
    public void sendRecord(final Record record, final boolean isManualRetry) {
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
        mailingRecordMapper.updateRecordStatusAndIncreaseCount(record.getId(), RecordStatus.FAILED.getValue(),
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
                                    mailingRecordMapper.updateRecordStatusAndIncreaseCount(record.getId(), RecordStatus.FAILED.getValue(),
                                            ((EmailSendException) e).getError().getReason(), false, null);
                                } else {
                                    mailingRecordMapper.updateRecordStatusAndIncreaseCount(record.getId(), RecordStatus.FAILED.getValue(),
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
            helper.setSubject(MimeUtility.encodeText(templateRender.renderTemplate(record.getSendData().getTemplate(), record.getSendData().getVariables(), TemplateRender.TemplateType.TITLE), ENCODE_UTF8, "B"));
            helper.setText(templateRender.renderTemplate(record.getSendData().getTemplate(), record.getSendData().getVariables(), TemplateRender.TemplateType.CONTENT), true);
            mailSender.send(msg);
            if (isManualRetry) {
                mailingRecordMapper.updateRecordStatusAndIncreaseCount(record.getId(), RecordStatus.COMPLETE.getValue(), null, true, new Date());
            } else {
                mailingRecordMapper.updateRecordStatusAndIncreaseCount(record.getId(), RecordStatus.COMPLETE.getValue(), null, false, null);
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
