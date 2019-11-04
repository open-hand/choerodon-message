package io.choerodon.notify.api.controller.v1;

import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.exception.FeignException;
import io.choerodon.notify.api.dto.NoticeSendDTO;
import io.choerodon.notify.api.service.NoticesSendService;
import io.choerodon.notify.api.service.WebSocketSendService;
import io.choerodon.notify.infra.dto.NotifyScheduleRecordDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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
     * NoticeSendDTO中目前未传输loginName 和 realName，所以发送通知前需要发起feign调用
     * 可以在NoticeSendDTO加入这些字段，则可以发送部分站内信时不需要feign调用
     */
    @PostMapping
    @ApiOperation(value = "发送消息")
    @Permission(type = ResourceType.SITE)
    public void postNotice(@RequestBody NoticeSendDTO dto) {
        if (StringUtils.isEmpty(dto.getCode())) {
            throw new FeignException("error.postNotify.codeEmpty");
        }
        if (dto.getSourceId() == null && dto.isSendingSMS()) {
            throw new FeignException("error.postNotify.sourceId.null");
        }
        if (dto.getSourceId() == null) {
            dto.setSourceId(0L);
        }
        if (dto.getParams() == null) {
            dto.setParams(new HashMap<>(0));
        }
        noticesSendService.sendNotice(dto);
    }

    /**
     * NoticeSendDTO中目前未传输loginName 和 realName，
     * 所以发送通知前需要发起feign调用，可以在NoticeSendDTO加入这些字段
     * 则可以发送部分站内信时不需要feign调用，定时发送信息
     *
     * @param dto
     */
    @PostMapping("/schedule")
    @ApiOperation(value = "发送定时邮件，定时站内信，定时短信")
    @Permission(type = ResourceType.SITE)
    public void postScheduleNotice(@RequestBody NoticeSendDTO dto,
                                   @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date date,
                                   @RequestParam String scheduleNoticeCode) {
        if (StringUtils.isEmpty(dto.getCode())) {
            throw new FeignException("error.postNotify.codeEmpty");
        }
        if (dto.getSourceId() == null && dto.isSendingSMS()) {
            throw new FeignException("error.postNotify.sourceId.null");
        }
        if (dto.getSourceId() == null) {
            dto.setSourceId(0L);
        }
        if (dto.getParams() == null) {
            dto.setParams(new HashMap<>(0));
        }
        noticesSendService.sendScheduleNotice(dto,date, scheduleNoticeCode);
    }

    @PutMapping("/schedule")
    @ApiOperation(value = "修改定时消息发送时间")
    @Permission(type = ResourceType.SITE)
    public void updateScheduleNotice(@RequestBody(required = false) NoticeSendDTO noticeSendDTO,
                                     @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")  Date date,
                                     @RequestParam String scheduleNoticeCode,
                                     @RequestParam Boolean isNewNotice) {
       noticesSendService.updateScheduleNotice(scheduleNoticeCode, date, noticeSendDTO, isNewNotice);
    }

    @DeleteMapping("/schdule")
    @ApiOperation(value = "删除定时消息")
    @Permission(type = ResourceType.SITE)
    public void deleteSchduleNotice(@RequestBody NotifyScheduleRecordDTO notifyScheduleRecordDTO) {
        noticesSendService.deleteScheduleNotice(notifyScheduleRecordDTO);
    }


    @PostMapping("/ws/{code}/{id}")
    @ApiOperation(value = "发送自定义消息到webSocket")
    @Permission(type = ResourceType.SITE)
    public void postWebSocket(@PathVariable("code") String code,
                              @PathVariable("id") String id,
                              @RequestBody String message) {
        if (StringUtils.isEmpty(message)) {
            LOGGER.warn("The message sent to webSocket is empty. code: {}, id: {}", code, id);
        } else {
            webSocketSendService.sendWebSocket(code, id, message);
        }
    }

}
