package io.choerodon.message.app.aop;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.ObjectUtils;
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

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Pointcut("execution (* org.hzero.message.app.service.impl.MessageGeneratorServiceImpl.generateMessage(org.hzero.boot.message.entity.DingTalkSender, org.hzero.message.domain.entity.Message))")
    public void modifyMessage() {

    }

    @AfterReturning(pointcut = "modifyMessage()", returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        Message message = (Message) result;
        String messageContent = ((Message) result).getPlainContent();
        if (!ObjectUtils.isEmpty(messageContent)) {
            message.setPlainContent(processMessageContent(messageContent));
        } else {
            messageContent = ((Message) result).getContent();
            message.setContent(processMessageContent(messageContent));
        }
    }

    private String processMessageContent(String message) {
        if ("/".endsWith(frontUrl)) {
            frontUrl = frontUrl.substring(0, frontUrl.length() - 1);
        }
        message = message.replaceAll("\\$\\{CHOERODON_FRONT_URL}", frontUrl);
        message = message + "\n\n发送时间: " + DATE_TIME_FORMATTER.format(LocalDateTime.now());
        return message;
    }
}
