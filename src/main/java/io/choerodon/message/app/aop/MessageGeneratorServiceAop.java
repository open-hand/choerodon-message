package io.choerodon.message.app.aop;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.hzero.message.domain.entity.Message;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MessageGeneratorServiceAop {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Pointcut("execution (* org.hzero.message.app.service.impl.MessageGeneratorServiceImpl.generateMessage(org.hzero.boot.message.entity.DingTalkSender, org.hzero.message.domain.entity.Message))")
    public void modifyMessage() {

    }

    @AfterReturning(pointcut = "modifyMessage()", returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        Message message = (Message) result;
        String plainContent = ((Message) result).getPlainContent();
        plainContent = plainContent + "\n\n发送时间: " + sdf.format(new Date());
        message.setPlainContent(plainContent);
    }
}
