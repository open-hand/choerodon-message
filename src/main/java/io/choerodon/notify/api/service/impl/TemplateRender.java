package io.choerodon.notify.api.service.impl;

import freemarker.template.TemplateException;
import io.choerodon.core.exception.CommonException;
import io.choerodon.notify.infra.dto.Template;
import io.choerodon.notify.infra.enums.SendingTypeEnum;
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


    public String renderTemplate(final Template template, final Map<String, Object> variables, TemplateType type) throws IOException, TemplateException {
        String messageType = template.getSendingType();
        String templateKey = template.getSendSettingCode() + "-" +
                messageType + ":" + type.getValue() + template.getObjectVersionNumber();
        freemarker.template.Template ft = freeMarkerConfigBuilder.getTemplate(templateKey);
        String content = "";
        switch (type) {
            case TITLE:
                if (SendingTypeEnum.EMAIL.getValue().equals(messageType)) {
                    content = template.getTitle();
                } else if (SendingTypeEnum.PM.getValue().equals(messageType)) {
                    content = template.getTitle();
                } else if (SendingTypeEnum.WH.getValue().equals(messageType)) {
                    content = template.getTitle();
                } else {
                    throw new CommonException("error.templateRender.renderError");
                }
                break;
            case CONTENT:
                if (SendingTypeEnum.EMAIL.getValue().equals(messageType)) {
                    content = template.getContent();
                } else if (SendingTypeEnum.PM.getValue().equals(messageType)) {
                    content = template.getContent();
                } else if (SendingTypeEnum.WH.getValue().equals(messageType)) {
                    content = template.getContent();
                } else if (SendingTypeEnum.SMS.getValue().equals(messageType)) {
                    content = template.getContent();
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
