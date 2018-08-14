package io.choerodon.notify.infra.utils;

import io.choerodon.notify.domain.Config;
import io.choerodon.notify.domain.MessageType;
import io.choerodon.notify.domain.Template;
import io.choerodon.swagger.notify.EmailTemplateScanData;
import org.springframework.boot.autoconfigure.mail.MailProperties;

public class ConvertUtils {

    private static final String EMAIL_SSL = "mail.smtp.ssl.enable";

    private static final String EMAIL_SEND_NAME = "mail.send.name";

    private ConvertUtils() {
    }

    public static Config mailProperties2Config(MailProperties mailProperties) {
        Config config = new Config();
        config.setEmailSendName(mailProperties.getUsername());
        config.setEmailAccount(mailProperties.getUsername());
        config.setEmailHost(mailProperties.getHost());
        config.setEmailPassword(mailProperties.getPassword());
        config.setEmailPort(mailProperties.getPort());
        config.setEmailProtocol(mailProperties.getProtocol());
        config.setEmailSsl(false);
        String ssl = mailProperties.getProperties().get(EMAIL_SSL);
        if (ssl != null) {
            config.setEmailSsl(Boolean.parseBoolean(ssl));
        }
        config.setEmailSendName(mailProperties.getProperties().get(EMAIL_SEND_NAME));
        return config;
    }

    public static Template convertEmailTemplate(final EmailTemplateScanData scanData) {
        Template template = new Template();
        template.setCode(scanData.getCode());
        template.setName(scanData.getName());
        template.setIsPredefined(true);
        template.setEmailTitle(scanData.getTitle());
        template.setMessageType(MessageType.EMAIL.getValue());
        template.setBusinessType(scanData.getBusinessType());
        template.setEmailContent(scanData.getContent());
        return template;
    }

}
