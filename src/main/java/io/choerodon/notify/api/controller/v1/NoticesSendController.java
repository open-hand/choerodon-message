package io.choerodon.notify.api.controller.v1;

import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.core.exception.FeignException;
import io.choerodon.notify.api.dto.NoticeSendDTO;
import io.choerodon.notify.api.service.NoticesSendService;
import io.choerodon.notify.api.service.WebSocketSendService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

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

    /**
     * NoticeSendDTO中目前未传输loginName 和 realName，
     * 所以发送通知前需要发起feign调用，可以在NoticeSendDTO加入这些字段
     * 则可以发送部分站内信时不需要feign调用
     * @param dto
     */
    @PostMapping
    @ApiOperation(value = "发送邮件，站内信，短信")
    @Permission(type = ResourceType.SITE)
    public void postNotice(@RequestBody NoticeSendDTO dto) {
        if (StringUtils.isEmpty(dto.getCode())) {
            throw new FeignException("error.postNotify.codeEmpty");
        }
        if (dto.getTargetUsers() == null || dto.getTargetUsers().isEmpty()) {
            return;
        }
        if (dto.getSourceId() == null) {
            dto.setSourceId(0L);
        }
        if (dto.getParams() == null) {
            dto.setParams(new HashMap<>(0));
        }
        noticesSendService.sendNotice(dto);
    }

    @PostMapping("/ws/{code}/{id}")
    @ApiOperation(value = "发送自定义消息到webSocket")
    @Permission(type = ResourceType.SITE)
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
