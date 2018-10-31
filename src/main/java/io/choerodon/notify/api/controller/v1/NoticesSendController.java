package io.choerodon.notify.api.controller.v1;

import io.choerodon.core.exception.FeignException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.notify.api.dto.EmailSendDTO;
import io.choerodon.notify.api.dto.NoticeSendDTO;
import io.choerodon.notify.api.dto.WsSendDTO;
import io.choerodon.notify.api.service.NoticesSendService;
import io.choerodon.notify.api.service.WebSocketSendService;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;

@RestController
@RequestMapping("v1/notices")
@Api("邮件，短信，站内信发送接口")
public class NoticesSendController {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoticesSendController.class);

    private NoticesSendService noticesSendService;

    private WebSocketSendService webSocketSendService;

    public NoticesSendController(NoticesSendService noticesSendService,
                                 WebSocketSendService webSocketSendService) {
        this.noticesSendService = noticesSendService;
        this.webSocketSendService = webSocketSendService;
    }

    @PostMapping
    @ApiOperation(value = "发送邮件，站内信，短信")
    @Permission(level = ResourceLevel.SITE)
    public void postNotice(@RequestBody NoticeSendDTO dto) {
        if (StringUtils.isEmpty(dto.getCode())) {
            throw new FeignException("error.postNotify.codeEmpty");
        }
        if (dto.getTargetUsers() == null || dto.getTargetUsers().isEmpty()) {
            return;
        }
        if (dto.getParams() == null) {
            dto.setParams(new HashMap<>(0));
        }
        noticesSendService.sendNotice(dto);
    }

    @PostMapping("/emails")
    @ApiOperation(value = "发送邮件")
    @Permission(level = ResourceLevel.SITE)
    public void postEmail(@RequestBody EmailSendDTO dto) {
        if (StringUtils.isEmpty(dto.getCode())) {
            throw new FeignException("error.noticeSend.codeEmpty");
        }
        if (StringUtils.isEmpty(dto.getDestinationEmail())) {
            throw new FeignException("error.noticeSend.emailEmpty");
        }
        noticesSendService.sendEmail(dto);
    }

    @PostMapping("/ws")
    @ApiOperation(value = "发送站内信到webSocket")
    @Permission(level = ResourceLevel.SITE)
    public void postSiteMessage(@RequestBody @Valid WsSendDTO dto) {
        if (dto.getParams() == null) {
            dto.setParams(new HashMap<>(0));
        }
        noticesSendService.sendSiteMessage(dto);
    }

    @PostMapping("/ws/{code}/{id}")
    @ApiOperation(value = "发送自定义消息到webSocket")
    @Permission(level = ResourceLevel.SITE)
    public void postWebSocket(@PathVariable("code") String code,
                              @PathVariable("id") String id,
                              @RequestBody String message) {
        if (StringUtils.isEmpty(message)) {
            LOGGER.info("The message sent to webSocket is empty. code: {}, id: {}", code, id);
        } else {
            webSocketSendService.sendWebSocket(code, id, message);
        }
    }

}
