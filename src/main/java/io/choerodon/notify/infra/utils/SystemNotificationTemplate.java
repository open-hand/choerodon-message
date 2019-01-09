package io.choerodon.notify.infra.utils;

import org.springframework.stereotype.Component;

import io.choerodon.core.notify.Level;
import io.choerodon.core.notify.NotifyBusinessType;
import io.choerodon.core.notify.PmTemplate;

@NotifyBusinessType(code = "systemNotification", name = "系统公告", level = Level.SITE,
        description = "系统全平台公告", isManualRetry = true)
@Component
public class SystemNotificationTemplate implements PmTemplate {
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
