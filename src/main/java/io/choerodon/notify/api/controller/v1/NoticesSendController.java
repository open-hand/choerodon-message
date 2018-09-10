package io.choerodon.notify.api.controller.v1;

import io.choerodon.core.exception.FeignException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.notify.api.dto.EmailSendDTO;
import io.choerodon.notify.api.dto.SiteMsgRecordDTO;
import io.choerodon.notify.api.dto.SiteMsgSendDTO;
import io.choerodon.notify.api.service.NoticesSendService;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

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
        noticesSendService.createMailSenderAndSendEmail(dto);
    }

    @PostMapping("/letters")
    @ApiOperation(value = "发送站内信")
    @Permission(level = ResourceLevel.SITE)
    public ResponseEntity<SiteMsgRecordDTO> postLetter(@RequestBody SiteMsgSendDTO siteMsgSendDTO) {
        SiteMsgRecordDTO siteMsgRecordDTO = new SiteMsgRecordDTO();
        siteMsgRecordDTO.setDeleted(false);
        siteMsgRecordDTO.setRead(false);
        siteMsgRecordDTO.setId(null);
        siteMsgRecordDTO.setSendTime(new Date());
        siteMsgRecordDTO.setObjectVersionNumber(null);
        validateSiteMsg(siteMsgSendDTO);
        siteMsgRecordDTO.setSiteMsgSend(siteMsgSendDTO);
        return new ResponseEntity<>(noticesSendService.sendSiteMsg(siteMsgRecordDTO), HttpStatus.OK);
    }

    private void validateSiteMsg(@RequestBody SiteMsgSendDTO siteMsgSendDTO) {
        if (siteMsgSendDTO == null) {
            throw new FeignException("error.noticeSend.siteMsgEmpty");
        }
        if (StringUtils.isEmpty(siteMsgSendDTO.getUserId())) {
            throw new FeignException("error.noticeSend.siteMsg.userIdEmpty");
        }
        if (StringUtils.isEmpty(siteMsgSendDTO.getTitle())) {
            throw new FeignException("error.noticeSend.siteMsg.titleEmpty");
        }
        if (StringUtils.isEmpty(siteMsgSendDTO.getContent())) {
            throw new FeignException("error.noticeSend.siteMsg.contentEmpty");
        }
    }
}
