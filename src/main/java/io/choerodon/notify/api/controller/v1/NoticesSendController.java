package io.choerodon.notify.api.controller.v1;

import io.choerodon.core.exception.FeignException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.notify.api.dto.EmailSendDTO;
import io.choerodon.notify.api.service.NoticesSendService;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/notices")
public class NoticesSendController {

    private NoticesSendService noticesSendService;

    public NoticesSendController(NoticesSendService noticesSendService) {
        this.noticesSendService = noticesSendService;
    }

    @PostMapping("/emails")
    @ApiOperation(value = "发送邮件")
    @Permission(level = ResourceLevel.SITE)
    public void postEmail(@RequestBody EmailSendDTO dto) {
        if (StringUtils.isEmpty(dto)) {
            throw new FeignException("error.noticeSend.codeEmpty");
        }
        noticesSendService.postEmail(dto);
    }


}
