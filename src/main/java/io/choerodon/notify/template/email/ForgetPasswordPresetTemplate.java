package io.choerodon.notify.template.email;

import io.choerodon.swagger.notify.EmailTemplate;
import org.springframework.stereotype.Component;

@Component
public class ForgetPasswordPresetTemplate implements EmailTemplate {

    @Override
    public String businessTypeCode() {
        return "forgetPassword";
    }

    @Override
    public String code() {
        return "forgetPassword-preset";
    }

    @Override
    public String name() {
        return "忘记密码";
    }

    @Override
    public String title() {
        return "验证邮件";
    }

    @Override
    public String content() {
        return "classpath://template/forgetPasswordPreset.html";
    }

}
