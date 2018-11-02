package io.choerodon.notify.api.service.impl;

import freemarker.template.TemplateException;
import io.choerodon.core.exception.CommonException;
import io.choerodon.notify.api.pojo.MessageType;
import io.choerodon.notify.domain.Template;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.util.Map;

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


    String renderTemplate(final Template template, final Map<String, Object> variables, TemplateType type)
            throws IOException, TemplateException {
        String templateKey = template.getCode() + ":" + type.getValue() + template.getObjectVersionNumber();
        freemarker.template.Template ft = freeMarkerConfigBuilder.getTemplate(templateKey);
        String content = "";
        switch (type) {
            case TITLE:
                content = template.getMessageType().equals(MessageType.EMAIL.getValue()) ?
                        template.getEmailTitle() : template.getPmTitle();
                break;
            case CONTENT:
                content = template.getMessageType().equals(MessageType.EMAIL.getValue()) ?
                        template.getEmailContent() : template.getPmContent();
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
