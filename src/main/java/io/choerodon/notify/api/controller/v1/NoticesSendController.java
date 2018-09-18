package io.choerodon.notify.api.controller.v1;

import io.choerodon.core.exception.FeignException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.notify.api.dto.EmailSendDTO;
import io.choerodon.notify.api.dto.WsSendDTO;
import io.choerodon.notify.api.service.NoticesSendService;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;

@RestController
@RequestMapping("v1/notices")
@Api("邮件，短信，站内信发送接口")
public class NoticesSendController {

    private NoticesSendService noticesSendService;

    public NoticesSendController(NoticesSendService noticesSendService) {
        this.noticesSendService = noticesSendService;
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
    @ApiOperation(value = "发送消息到webSocket")
    @Permission(level = ResourceLevel.SITE)
    public void postPm(@RequestBody @Valid WsSendDTO dto) {
        if (dto.getParams() == null) {
            dto.setParams(new HashMap<>(0));
        }
        noticesSendService.sendWs(dto);
    }

}
