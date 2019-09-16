package io.choerodon.notify.api.service.impl;

import java.io.IOException;
import java.util.Map;

import freemarker.template.TemplateException;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import io.choerodon.core.exception.CommonException;
import io.choerodon.notify.api.pojo.MessageType;
import io.choerodon.notify.domain.Template;

@Component
public class TemplateRender {

    enum TemplateType {
        CONTENT("content"),
        TITLE("title");
        private String value;

        public String getValue() {
            return value;
        }

        TemplateType(String value) {
            this.value = value;
        }
    }

    private final FreeMarkerConfigBuilder freeMarkerConfigBuilder;

    public TemplateRender(FreeMarkerConfigBuilder freeMarkerConfigBuilder) {
        this.freeMarkerConfigBuilder = freeMarkerConfigBuilder;
    }


    public String renderTemplate(final Template template, final Map<String, Object> variables, TemplateType type) throws IOException, TemplateException {
        String messageType = template.getMessageType();
        String templateKey = template.getCode() + "-" + messageType + ":" + type.getValue() + template.getObjectVersionNumber();
        freemarker.template.Template ft = freeMarkerConfigBuilder.getTemplate(templateKey);
        String content = "";
        switch (type) {
            case TITLE:
                if (MessageType.EMAIL.getValue().equals(messageType)){
                    content = template.getEmailTitle();
                } else if (MessageType.PM.getValue().equals(messageType)){
                    content = template.getPmTitle();
                } else {
                    throw new CommonException("error.templateRender.renderError");
                }
                break;
            case CONTENT:
                if (MessageType.EMAIL.getValue().equals(messageType)){
                    content = template.getEmailContent();
                } else if (MessageType.PM.getValue().equals(messageType)){
                    content = template.getPmContent();
                } else if (MessageType.WH.getValue().equals(messageType)){
                    content = template.getWhContent();
                } else if (MessageType.SMS.getValue().equals(messageType)){
                    content = template.getSmsContent();
                } else {
                    throw new CommonException("error.templateRender.renderError");
                }
                break;
        }
        if (ft == null) {
            ft = freeMarkerConfigBuilder.addTemplate(templateKey, content);
        }
        if (ft == null) {
            throw new CommonException("error.templateRender.renderError");
        }
        return FreeMarkerTemplateUtils.processTemplateIntoString(ft, variables);
    }
}
