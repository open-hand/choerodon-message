package io.choerodon.message.app.aop;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.hzero.message.domain.entity.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MessageGeneratorServiceAop {
    @Value("${services.front.url}")
    private String frontUrl;

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);

    @Pointcut("execution (* org.hzero.message.app.service.impl.MessageGeneratorServiceImpl.generateMessage(org.hzero.boot.message.entity.DingTalkSender, org.hzero.message.domain.entity.Message))")
    public void modifyMessage() {

    }

    @AfterReturning(pointcut = "modifyMessage()", returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        Message message = (Message) result;
        String plainContent = ((Message) result).getPlainContent();
        if ("/".endsWith(frontUrl)) {
            frontUrl = frontUrl.substring(0, frontUrl.length() - 1);
        }
        plainContent = plainContent.replaceAll("\\$\\{CHOERODON_FRONT_URL}", frontUrl);
        plainContent = plainContent + "\n\n发送时间: " + dtf.format(LocalDateTime.now());
        message.setPlainContent(plainContent);
    }
}
