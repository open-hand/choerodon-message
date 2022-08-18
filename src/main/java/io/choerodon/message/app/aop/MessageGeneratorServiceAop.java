package io.choerodon.message.app.aop;

import static io.choerodon.message.infra.constant.Constants.*;

import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.hzero.common.HZeroService;
import org.hzero.core.redis.safe.SafeRedisHelper;
import org.hzero.message.app.service.DingTalkServerService;
import org.hzero.message.domain.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;
import io.choerodon.message.infra.feign.IamFeignClient;

@Aspect
@Component
public class MessageGeneratorServiceAop {
    private static final String REG_STRING = "\\[([\\u4e00-\\u9fa5\\w\\W]+)\\]\\(([\\u4e00-\\u9fa5\\w\\W]*)\\)";

    private static final Pattern URL_PATTERN = Pattern.compile(REG_STRING);

    private static final String DING_TALK_URL_TEMPLATE = "[%s](dingtalk://dingtalkclient/action/openapp?corpid=%s&container_type=work_platform&app_id=0_%s&redirect_type=jump&redirect_url=%s)";

    @Value("${services.front.url}")
    private String frontUrl;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private IamFeignClient iamFeignClient;

    @Autowired
    private DingTalkServerService dingTalkServerService;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Pointcut("execution (* org.hzero.message.app.service.impl.MessageGeneratorServiceImpl.generateMessage(org.hzero.boot.message.entity.DingTalkSender, org.hzero.message.domain.entity.Message))")
    public void modifyMessage() {

    }

    @AfterReturning(pointcut = "modifyMessage()", returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        Message message = (Message) result;
        message.setTemplateEditType("RT");
        String messageContent = ((Message) result).getPlainContent();
        if (!ObjectUtils.isEmpty(messageContent)) {
            message.setPlainContent(processMessageContent(messageContent, message.getTenantId()));
        } else {
            messageContent = ((Message) result).getContent();
            message.setContent(processMessageContent(messageContent, message.getTenantId()));
        }
    }

    public String processMessageContent(String message, Long tenantId) {
        if ("/".endsWith(frontUrl)) {
            frontUrl = frontUrl.substring(0, frontUrl.length() - 1);
        }
        // 替换前端地址
        message = message.replaceAll("\\$\\{CHOERODON_FRONT_URL}", frontUrl);

        // 如果是内部浏览器跳转
        if (Boolean.TRUE.equals(isInternalBrowser(tenantId, DING_TALK_OPEN_APP_CODE))) {
            // 替换成钉钉内部应用打开格式
            Matcher matcher = URL_PATTERN.matcher(message);
            try {
                if (matcher.find()) {
                    String corpId = getCorpIdByOrgId(tenantId);
                    Long agentId = dingTalkServerService.getDefaultAgentId(tenantId, "DING_TALK");
                    String urlText = matcher.group(1);
                    String encodedUrl = URLEncoder.encode(matcher.group(2), "UTF-8");
                    String finalJumpUrl = String.format(DING_TALK_URL_TEMPLATE, urlText, corpId, agentId.toString(), encodedUrl);
                    message = message.replaceAll(REG_STRING, finalJumpUrl);
                }
            } catch (Exception e) {
                throw new CommonException(e);
            }
        }

        // 添加后缀，避免产生重复消息
        message = message + "\n\n发送时间: " + DATE_TIME_FORMATTER.format(LocalDateTime.now());
        return message;
    }

    private String getCorpIdByOrgId(Long orgId) {
        String redisKey = String.format(REDIS_KEY_CORP_ID, orgId);
        String corpId = stringRedisTemplate.opsForValue().get(redisKey);
        if (StringUtils.isEmpty(corpId)) {
            corpId = iamFeignClient.queryDingTalkCorpId(orgId).getBody();
            stringRedisTemplate.opsForValue().set(redisKey, corpId, 3600, TimeUnit.SECONDS);
        }
        return corpId;
    }

    private Boolean isInternalBrowser(Long tenantId, String typeCode) {
        AtomicReference<Boolean> result = new AtomicReference<>();
        SafeRedisHelper.execute(HZeroService.Message.REDIS_DB, helper -> {
            String redisKey = String.format(REDIS_KEY_INTERNAL_MESSAGE, typeCode, tenantId);
            String s = helper.strGet(redisKey);
            if (s != null) {
                result.set(Boolean.parseBoolean(s));
            }
        });
        if (result.get() == null) {
            Boolean internalBrowser = iamFeignClient.isInternalBrowser(tenantId, typeCode).getBody();
            result.set(internalBrowser);
        }
        return result.get();
    }
}
