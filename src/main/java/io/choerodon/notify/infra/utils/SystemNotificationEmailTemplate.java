package io.choerodon.notify.infra.utils;

import org.springframework.stereotype.Component;

import io.choerodon.core.notify.EmailTemplate;

@Component
public class SystemNotificationEmailTemplate implements EmailTemplate {
    @Override
    public String businessTypeCode() {
        return "systemNotification";
    }

    @Override
    public String code() {
        return "systemNotification-preset";
    }

    @Override
    public String name() {
        return "系统公告";
    }

    @Override
    public String title() {
        return "${title}";
    }

    @Override
    public String content() {
        return "${content}";
    }
}
