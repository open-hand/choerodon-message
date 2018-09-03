package io.choerodon.notify.api.pojo;

import io.choerodon.notify.domain.Template;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Map;

public class RecordSendData {

    private Template template;

    private Map<String, Object> variables;

    private JavaMailSenderImpl mailSender;

    private Integer maxRetryCount;

    public RecordSendData() {
    }

    public RecordSendData(Template template, Map<String, Object> variables,
                          JavaMailSenderImpl mailSender, Integer maxRetryCount) {
        this.template = template;
        this.variables = variables;
        this.mailSender = mailSender;
        this.maxRetryCount = maxRetryCount;
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public JavaMailSenderImpl getMailSender() {
        return mailSender;
    }

    public void setMailSender(JavaMailSenderImpl mailSender) {
        this.mailSender = mailSender;
    }

    public Integer getMaxRetryCount() {
        return maxRetryCount;
    }

    public void setMaxRetryCount(Integer maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }
}
