package io.choerodon.notify.api.service.impl;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.choerodon.core.exception.FeignException;
import io.choerodon.notify.api.dto.EmailSendDTO;
import io.choerodon.notify.api.service.NoticesSendService;
import io.choerodon.notify.domain.Config;
import io.choerodon.notify.domain.SendSetting;
import io.choerodon.notify.infra.mapper.ConfigMapper;
import io.choerodon.notify.infra.mapper.SendSettingMapper;
import io.choerodon.notify.infra.mapper.TemplateMapper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.StringUtils;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.IOException;
import java.util.Map;

@Service
public class NoticesSendServiceImpl implements NoticesSendService {

    private final JavaMailSender mailSender;

    private final SendSettingMapper sendSettingMapper;

    private final TemplateMapper templateMapper;

    private final ConfigMapper configMapper;

    private static final String ENCODE_UTF8 = "utf-8";

    public NoticesSendServiceImpl(JavaMailSender mailSender,
                                  SendSettingMapper sendSettingMapper,
                                  ConfigMapper configMapper,
                                  TemplateMapper templateMapper) {
        this.mailSender = mailSender;
        this.sendSettingMapper = sendSettingMapper;
        this.configMapper = configMapper;
        this.templateMapper = templateMapper;
    }

    @Override
    public void postEmail(EmailSendDTO dto) {
        Config config = configMapper.selectOne(new Config());
        if (config == null || StringUtils.isEmpty(config.getEmailAccount())) {
            throw new FeignException("error.noticeSend.emailConfigNotSet");
        }
        SendSetting sendSetting =  sendSettingMapper.selectOne(new SendSetting(dto.getCode()));
        if (dto.getCode() == null || sendSetting == null) {
            throw new FeignException("error.noticeSend.codeNotFound");
        }
        if (sendSetting.getEmailTemplateId() == null) {
            throw new FeignException("error.noticeSend.emailTemplateNotSet");
        }
        io.choerodon.notify.domain.Template template = templateMapper.selectByPrimaryKey(sendSetting.getEmailTemplateId());
        if (template == null){
            throw new FeignException("error.noticeSend.emailTemplateNotSet");
        }
        sendEmail(config, dto, template);
    }

    private void sendEmail(final Config config, final EmailSendDTO dto, final io.choerodon.notify.domain.Template template) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, ENCODE_UTF8);
            helper.setFrom(new InternetAddress("\"" + MimeUtility.encodeText(config.getEmailSendName()) + "\"<" + config.getEmailAccount() + ">"));
            helper.setTo(dto.getEmailAddress());
            helper.setSubject(MimeUtility.encodeText(template.getEmailTitle(), ENCODE_UTF8, "B"));
            helper.setText(getMailText(template.getEmailContent(), dto.getVariables()), true);
            mailSender.send(msg);
        } catch (Exception e) {
           throw new FeignException("error.noticeSend.emailSendError", e);
        }

    }

    private String getMailText(final String context, final  Map<String, Object> variables) throws IOException, TemplateException {
        Template template = Template.getPlainTextTemplate("", context, new Configuration(Configuration.VERSION_2_3_28));
        return FreeMarkerTemplateUtils.processTemplateIntoString(template, variables);
    }


}
