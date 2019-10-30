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
import io.choerodon.notify.api.service.TemplateService;
import io.choerodon.notify.domain.Record;
import io.choerodon.notify.infra.cache.ConfigCache;
import io.choerodon.notify.infra.dto.Config;
import io.choerodon.notify.infra.dto.MailingRecordDTO;
import io.choerodon.notify.infra.dto.SendSettingDTO;
import io.choerodon.notify.infra.dto.Template;
import io.choerodon.notify.infra.enums.SendingTypeEnum;
import io.choerodon.notify.infra.mapper.MailingRecordMapper;
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
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Service
public class EmailSendServiceImpl implements EmailSendService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoticesSendService.class);

    private final TemplateService templateService;

    private final ConfigCache configCache;

    private static final String ENCODE_UTF8 = "utf-8";

    private final TemplateRender templateRender;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final MailingRecordMapper mailingRecordMapper;

    private final EmailQueueObservable emailQueueObservable;

    private final Executor executor;


    public EmailSendServiceImpl(TemplateService templateService,
                                ConfigCache configCache,
                                MailingRecordMapper mailingRecordMapper,
                                EmailQueueObservable emailQueueObservable,
                                TemplateRender templateRender,
                                @Qualifier("asyncSendNoticeExecutor") Executor executor) {
        this.templateService = templateService;
        this.configCache = configCache;
        this.mailingRecordMapper = mailingRecordMapper;
        this.emailQueueObservable = emailQueueObservable;
        this.templateRender = templateRender;
        this.executor = executor;
    }


    @Override
    public void sendEmail(Map<String, Object> params, Set<UserDTO> targetUsers, SendSettingDTO sendSettingDTO) {
        LOGGER.warn(">>>START_SENDING_EMAIL>>> Send a message to the user.[INFO:send_setting_code:'{}' - users:{} ]", sendSettingDTO.getCode(), targetUsers);

        //1. 获取该发送设置的邮件模版
        Template tmp = null;
        try {
            tmp = templateService.getOne(new Template()
                    .setSendingType(SendingTypeEnum.EMAIL.getValue())
                    .setSendSettingCode(sendSettingDTO.getCode()));
        } catch (Exception e) {
            LOGGER.warn(">>>CANCEL_SENDING_EMAIL>>> No valid templates available.");
            return;
        }
        Template template = tmp;
        //2.逐个发送邮件
        targetUsers.forEach(user -> {
            Record mailingRecordDTO = (Record) new MailingRecordDTO()
                    .setRetryCount(0)
                    .setReceiveAccount(user.getEmail())
                    .setSendSettingCode(sendSettingDTO.getCode())
                    .setTemplateId(template.getId());
            //2.1.整理参数
            Map<String, Object> newParams = DefaultAutowiredField.autowiredDefaultParams(params, user);
            mailingRecordDTO.setVariables(ConvertUtils.convertMapToJson(objectMapper, newParams));
            //2.2.记录邮件发送信息
            if (mailingRecordMapper.insert(mailingRecordDTO) != 1) {
                throw new CommonException("error.noticeSend.recordInsert");
            }
            //2.3.发送邮件
            mailingRecordDTO.setSendData(new RecordSendData(template, newParams, createEmailSender(), sendSettingDTO.getRetryCount()));
            if (sendSettingDTO.getIsSendInstantly()) {
                sendRecord(mailingRecordDTO, false);
            } else {
                emailQueueObservable.emit(mailingRecordDTO);
            }
        });
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
