package io.choerodon.notify.infra.template;

import io.choerodon.core.notify.Level;
import io.choerodon.core.notify.NotifyBusinessType;
import io.choerodon.core.notify.PmTemplate;

import org.springframework.stereotype.Component;

@NotifyBusinessType(code = "systemNotification", name = "系统公告", level = Level.SITE,
        emailEnabledFlag = true, pmEnabledFlag = true,
        description = "系统全平台公告", isManualRetry = true, categoryCode = "sys-management")
@Component
public class SystemNotificationPmTemplate implements PmTemplate {
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
