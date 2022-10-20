package io.choerodon.message.infra.constant;

/**
 * 一些常量
 */
public class Constants {
    public static final String DING_TALK_OPEN_APP_CODE = "ding_talk";

    public static final String SITE = "site";

    public static final String DING_TALK_SERVER_CODE = "DING_TALK";

    public static final String REDIS_KEY_CORP_ID = "corp-id-%s";

    public static final String REDIS_KEY_INTERNAL_MESSAGE = "open-app-internal-message:%s:%s";

    public static final String REDIS_KEY_SYSTEM_MESSAGE = "open-app-system-message:%s:%s";

    public static class EmailTemplateConstants {
        public EmailTemplateConstants() {
        }

        public static final String EMAIL_TEMPLATE_LOGO = "choerodonLogo";

        public static final String EMAIL_TEMPLATE_SLOGAN = "choerodonSlogan";

        public static final String EMAIL_TEMPLATE_FOOTER = "choerodonFooter";

    }

}
