package org.hzero.message.infra.supporter;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.property.BodyType;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.message.entity.Attachment;
import org.hzero.core.base.BaseConstants;
import org.hzero.message.domain.entity.EmailProperty;
import org.hzero.message.domain.entity.EmailServer;
import org.hzero.message.domain.entity.Message;
import org.hzero.message.infra.constant.HmsgConstant;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import io.choerodon.core.exception.CommonException;

/**
 * <p>
 * 邮件发送支持
 * </p>
 * 临时覆盖sendEmail setFrom
 * @author qingsheng.chen 2019/1/21 星期一 20:17
 */
public class EmailSupporter {

    private EmailSupporter() {
    }

    public static JavaMailSender javaMailSender(EmailServer emailServer) {
        if (HmsgConstant.ProtocolType.EXCHANGE.equals(emailServer.getProtocol())) {
            return null;
        }
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(emailServer.getHost());
        javaMailSender.setPort(Integer.parseInt(emailServer.getPort()));
        javaMailSender.setUsername(emailServer.getUsername());
        javaMailSender.setPassword(emailServer.getPasswordEncrypted());
        if (StringUtils.isNotBlank(emailServer.getProtocol())) {
            javaMailSender.setProtocol(emailServer.getProtocol());
        }
        if (CollectionUtils.isNotEmpty(emailServer.getEmailProperties())) {
            Properties properties = new Properties();
            emailServer.getEmailProperties().forEach(item -> properties.setProperty(item.getPropertyCode(), item.getPropertyValue()));
            javaMailSender.setJavaMailProperties(properties);
        }
        return javaMailSender;
    }

    public static void sendEmail(JavaMailSender javaMailSender, EmailServer emailServer, Message message, List<String> to, Integer batchSend) throws MessagingException {
        // 明文消息不存在，使用content
        String mailContent = StringUtils.isEmpty(message.getPlainContent()) ? message.getContent() : message.getPlainContent();
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, BaseConstants.DEFAULT_CHARSET);
//        mimeMessageHelper.setFrom(emailServer.getSender());
        try {
            mimeMessageHelper.setFrom(new InternetAddress(emailServer.getUsername(), emailServer.getSender()));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new CommonException(e.getMessage(), e);
        }
        mimeMessageHelper.setSubject(message.getSubject());
        // 富文本模板使用html发送
        if (HmsgConstant.TemplateEditType.MARKDOWN.equals(message.getTemplateEditType())) {
            mimeMessageHelper.setText(mailContent);
        } else {
            mimeMessageHelper.setText(mailContent, true);
        }
        // 附件
        if (!CollectionUtils.isEmpty(message.getAttachmentList())) {
            message.getAttachmentList().forEach(item -> {
                try {
                    mimeMessageHelper.addAttachment(item.getFileName(), new ByteArrayResource(item.getFile()));
                } catch (Exception e) {
                    throw new CommonException(e);
                }
            });
        }
        // 抄送
        if (CollectionUtils.isNotEmpty(message.getCcList())) {
            mimeMessageHelper.setCc(message.getCcList().toArray(new String[]{}));
        }
        // 密送
        if (CollectionUtils.isNotEmpty(message.getBccList())) {
            mimeMessageHelper.setBcc(message.getBccList().toArray(new String[]{}));
        }
        if (Objects.equals(batchSend, BaseConstants.Flag.NO)) {
            for (String email : to) {
                mimeMessageHelper.setTo(email);
                javaMailSender.send(mimeMessage);
            }
        } else {
            mimeMessageHelper.setTo(to.toArray(new String[]{}));
            javaMailSender.send(mimeMessage);
        }
    }

    public static ExchangeService exchangeService(EmailServer emailServer) {
        if (!HmsgConstant.ProtocolType.EXCHANGE.equals(emailServer.getProtocol())) {
            return null;
        }
        List<EmailProperty> emailProperties = emailServer.getEmailProperties();
        String domain = null;
        String version = null;

        for (EmailProperty properties : emailProperties) {
            if (HmsgConstant.ExchangeProperties.DOMAIN.equals(properties.getPropertyCode())) {
                domain = properties.getPropertyValue();
            }
            if (HmsgConstant.ExchangeProperties.EXCHANGE_VERSION.equals(properties.getPropertyCode())) {
                version = properties.getPropertyValue();
            }
        }

        if (StringUtils.isEmpty(version)) {
            throw new CommonException(HmsgConstant.ErrorCode.EXCHANGE_VERSION_EMPTY);
        }
        // Exchange服务器版本。
        ExchangeService exchangeService = new ExchangeService(buildExcVersion(version));

        // 要在MS Exchange服务器上签名的凭据。
        ExchangeCredentials exchangeCredentials = new WebCredentials(emailServer.getUsername(), emailServer.getPasswordEncrypted(), domain);
        exchangeService.setCredentials(exchangeCredentials);

        // 邮箱的exchange web服务的URL
        try {
            exchangeService.setUrl(new URI(emailServer.getHost()));
        } catch (URISyntaxException ex) {
            exchangeService.close();
            throw new CommonException(HmsgConstant.ErrorCode.EXCHANGE_CONNECT_FAILED, ex);
        }
        return exchangeService;
    }

    public static void sendExchange(ExchangeService exchangeService, EmailServer emailServer, Message message, List<String> to, Integer batchSend) {
        // 设置邮件信息
        EmailMessage emailMessage;
        try {
            // 明文消息不存在，使用content
            emailMessage = new EmailMessage(exchangeService);
            emailMessage.setSubject(message.getSubject());
            if (HmsgConstant.TemplateEditType.MARKDOWN.equals(message.getTemplateEditType())) {
                emailMessage.setBody(new MessageBody(BodyType.Text, message.getContent()));
            } else {
                emailMessage.setBody(new MessageBody(BodyType.HTML, message.getContent()));
            }
            // 设置收件人
            for (String toRecipient : to) {
                emailMessage.getToRecipients().add(toRecipient);
            }
            // 设置抄送人
            if (CollectionUtils.isNotEmpty(message.getCcList())) {
                for (String recipient : message.getCcList()) {
                    emailMessage.getCcRecipients().add(recipient);

                }
            }
            // 设置邮件密送人
            if (CollectionUtils.isNotEmpty(message.getBccList())) {
                for (String recipient : message.getBccList()) {
                    emailMessage.getBccRecipients().add(recipient);

                }
            }
            // 设置附件
            if (CollectionUtils.isNotEmpty(message.getAttachmentList())) {
                for (Attachment attachment : message.getAttachmentList()) {
                    emailMessage.getAttachments().addFileAttachment(attachment.getFileName(), attachment.getFile());

                }
            }
            emailMessage.send();
        } catch (Exception ex) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR, ex);
        }
    }

    private static ExchangeVersion buildExcVersion(String version) {
        switch (version) {
            case "Exchange2007_SP1":
                return ExchangeVersion.Exchange2007_SP1;
            case "Exchange2010_SP1":
                return ExchangeVersion.Exchange2010_SP1;
            case "Exchange2010_SP2":
                return ExchangeVersion.Exchange2010_SP2;
            case "Exchange2010":
            default:
                return ExchangeVersion.Exchange2010;

        }
    }
}
