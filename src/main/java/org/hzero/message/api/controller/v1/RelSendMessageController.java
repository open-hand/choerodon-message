package org.hzero.message.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hzero.boot.message.config.MessageClientProperties;
import org.hzero.boot.message.entity.AllSender;
import org.hzero.boot.message.entity.DingTalkSender;
import org.hzero.boot.message.entity.MessageSender;
import org.hzero.boot.message.entity.WeChatSender;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.message.app.service.RelSendMessageService;
import org.hzero.message.config.MessageSwaggerApiConfig;
import org.hzero.message.domain.entity.Message;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

/**
 * <p>
 * 关联消息发送
 * </p>
 *
 * @author qingsheng.chen 2018/10/7 星期日 15:41
 */
@Api(tags = MessageSwaggerApiConfig.REL_MESSAGE)
@RestController("relSendController.v1")
@RequestMapping("/v1/{organizationId}/message/relevance")
public class RelSendMessageController extends BaseController {

    private final RelSendMessageService relSendMessageService;
    private final MessageClientProperties messageClientProperties;

    @Autowired
    public RelSendMessageController(RelSendMessageService relSendMessageService,
                                    MessageClientProperties messageClientProperties) {
        this.relSendMessageService = relSendMessageService;
        this.messageClientProperties = messageClientProperties;
    }

    @ApiOperation(value = "关联发送消息，邮件/短信/站内信(返回发送结果，若服务端开启了异步发送则不返回)")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/with-receipt")
    public ResponseEntity<List<Message>> sendMessageWithReceipt(@PathVariable("organizationId") Long organizationId, @RequestBody @Encrypt MessageSender messageSender) {
        messageSender.setTenantId(organizationId);
        messageSender.getMessageMap().clear();
        validObject(messageSender);
        return Results.success(relSendMessageService.relSendMessageReceipt(messageSender));
    }
}
