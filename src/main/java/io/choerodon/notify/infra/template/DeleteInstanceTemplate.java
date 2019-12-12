package io.choerodon.notify.infra.template;

import org.springframework.stereotype.Component;

import io.choerodon.core.notify.SmsTemplate;

/**
 * @author superlee
 * @since 2019-05-17
 */
@Component
public class DeleteInstanceTemplate implements SmsTemplate {
    @Override
    public String businessTypeCode() {
        return "resourceDeleteConfirmation";
    }

    @Override
    public String code() {
        return "devops-delete-instance";
    }

    @Override
    public String name() {
        return "删除实例发送短信";
    }

    @Override
    public String title() {
        return "华润置地_Choerodon删除实例验证码";
    }

    /**
     * 短信模版内容
     */
    @Override
    public String content() {
        return "{\"businessCode\":\"华润置地_Choerodon删除实例验证码\",\"mobile\":\"${mobile}\",\"content\":{\"user\":\"${user}\",\"env\":\"${env}\",\"instance\":\"${instance}\",\"captcha\":\"${captcha}\",\"timeout\":\"${timeout}\"},\"secretKey\":\"${secretKey}\",\"source\":\"Choerodon猪齿鱼\",\"sourceNote\":\"Choerodon猪齿鱼短信验证码\"}";
    }

    @Override
    public String type() {
        return "sms";
    }
}
