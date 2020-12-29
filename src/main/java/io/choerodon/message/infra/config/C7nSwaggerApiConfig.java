package io.choerodon.message.infra.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.service.Tag;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * <p>
 * Swagger Api 描述配置
 * </p>
 *
 * @author qingsheng.chen 2018/7/30 星期一 14:26
 */
@Configuration
public class C7nSwaggerApiConfig {
    public static final String CHOERODON_WEBHOOK_ORGANIZATION = "Choerodon Webhook Organization";
    public static final String CHOERODON_WEBHOOK_PROJECT = "Choerodon Webhook Project";
    public static final String CHOERODON_CONFIG = "Choerodon Config";
    public static final String CHOERODON_MAIL_RECORD = "Choerodon Mail Record";
    public static final String CHOERODON_MESSAGE_SETTING = "Choerodon Message Setting";
    public static final String CHOERODON_RECEIVE_SETTING = "Choerodon Receive Setting";
    public static final String CHOERODON_SEND_SETTING = "Choerodon Send Setting";
    public static final String CHOEROODN_USER_MESSAGES = "Choerodon User Messages";
    public static final String CHOEROODN_PROJECT_MAILSEND = "choeroodn project mailsend";


    @Autowired
    public C7nSwaggerApiConfig(Docket docket) {
        docket.tags(
                new Tag(CHOERODON_WEBHOOK_ORGANIZATION, "Choerodon租户层webhook"),
                new Tag(CHOERODON_WEBHOOK_PROJECT, "Choerodon项目层webhook"),
                new Tag(CHOERODON_CONFIG, "Choerodon邮件短信配置"),
                new Tag(CHOERODON_MAIL_RECORD, "Choerodon邮件记录"),
                new Tag(CHOERODON_MESSAGE_SETTING, "choerodon消息项目层设置"),
                new Tag(CHOERODON_RECEIVE_SETTING, "choerodon消息接收设置"),
                new Tag(CHOEROODN_USER_MESSAGES, "choerodon用户站内信"),
                new Tag(CHOERODON_SEND_SETTING, "choerodon消息发送设置"),
                new Tag(CHOEROODN_PROJECT_MAILSEND, "choerodon项目层消息发送")
        );
    }
}
