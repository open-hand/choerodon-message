package io.choerodon.notify.template.email;

import io.choerodon.swagger.notify.EmailTemplate;
import org.springframework.stereotype.Component;

@Component
public class VerificationCodeTemplate implements EmailTemplate {

    @Override
    public String businessTypeCode() {
        return "verification-code";
    }

    @Override
    public String code() {
        return "verification-code-template";
    }

    @Override
    public String name() {
        return "notify验证码";
    }

    @Override
    public String title() {
        return "notify验证码";
    }

    @Override
    public String content() {
        return "<p>您收到的验证码为：${code}</p>";
    }

}
