package io.choerodon.notify.api.service.impl;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class FreeMarkerConfigBuilder {

    private static final String ENCODE_UTF8 = "utf-8";

    private Configuration config;
    private StringTemplateLoader stringTemplateLoader;

    public FreeMarkerConfigBuilder() {
        config = new Configuration(Configuration.VERSION_2_3_28);
        stringTemplateLoader = new StringTemplateLoader();
        config.setTemplateLoader(stringTemplateLoader);
    }

    Template addTemplate(String key, String template) {
        stringTemplateLoader.putTemplate(key, template, System.currentTimeMillis());
        config.clearTemplateCache();
        try {
            return config.getTemplate(key, ENCODE_UTF8);
        } catch (IOException e) {
            return null;
        }
    }

    Template getTemplate(String key) {
        try {
            return config.getTemplate(key, ENCODE_UTF8);
        } catch (IOException e) {
            return null;
        }
    }


}
