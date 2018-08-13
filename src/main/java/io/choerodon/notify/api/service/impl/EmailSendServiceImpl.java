package io.choerodon.notify.api.service.impl;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.choerodon.notify.api.service.EmailSendService;
import io.choerodon.notify.api.service.VariableService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailSendServiceImpl implements EmailSendService {

    private final JavaMailSender mailSender;

    private final VariableService variableService;

    private static final String ENCODE_UTF8 = "utf-8";

    public EmailSendServiceImpl(JavaMailSender mailSender,
                                VariableService variableService) {
        this.mailSender = mailSender;
        this.variableService = variableService;
    }

    @Override
    public void sendTest() {
        MimeMessage msg = null;
        try {
            msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, ENCODE_UTF8);
            helper.setFrom("service@estore.com");
            helper.setTo("zhangsan@estore.com");
            helper.setSubject(MimeUtility.encodeText("estore注册成功提示邮件", ENCODE_UTF8, "B"));
            helper.setText(getMailText(), true);
            helper.addInline("welcomePic", new File("d:/welcome.gif"));
            File file = new File("d:/欢迎注册.docx");
            helper.addAttachment(MimeUtility.encodeWord(file.getName()), file);
        } catch (Exception e) {
        }
        mailSender.send(msg);
    }


    private String getMailText() throws IOException, TemplateException {
        Template template = Template.getPlainTextTemplate("", "", new Configuration(Configuration.VERSION_2_3_28));
        return FreeMarkerTemplateUtils.processTemplateIntoString(template, getVariableMap());
    }

    private Map<String, String> getVariableMap() {
        return new HashMap<>();
    }


}
