package io.choerodon.notify.api.service.impl;

import freemarker.template.TemplateException;
import io.choerodon.core.exception.CommonException;
import io.choerodon.notify.domain.Template;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.util.Map;

@Component
public class TemplateRender {

    private final FreeMarkerConfigBuilder freeMarkerConfigBuilder;

    public TemplateRender(FreeMarkerConfigBuilder freeMarkerConfigBuilder) {
        this.freeMarkerConfigBuilder = freeMarkerConfigBuilder;
    }

    String renderTemplate(final Template template, final Map<String, Object> variables)
            throws IOException, TemplateException {
        String templateKey = template.getCode() + ":" + template.getObjectVersionNumber();
        freemarker.template.Template ft = freeMarkerConfigBuilder.getTemplate(templateKey);
        if (ft == null) {
            ft = freeMarkerConfigBuilder.addTemplate(templateKey, template.getEmailContent());
        }
        if (ft == null) {
            throw new CommonException("error.templateRender.renderError");
        }
        return FreeMarkerTemplateUtils.processTemplateIntoString(ft, variables);
    }

    String renderPmTemplate(final Template template, final Map<String, Object> variables)
            throws IOException, TemplateException {
        String templateKey = template.getCode() + ":" + template.getObjectVersionNumber();
        freemarker.template.Template ft = freeMarkerConfigBuilder.getTemplate(templateKey);
        if (ft == null) {
            ft = freeMarkerConfigBuilder.addTemplate(templateKey, template.getPmContent());
        }
        if (ft == null) {
            throw new CommonException("error.templateRender.renderError");
        }
        return FreeMarkerTemplateUtils.processTemplateIntoString(ft, variables);
    }
}
