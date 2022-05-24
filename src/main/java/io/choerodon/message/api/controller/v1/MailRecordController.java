package io.choerodon.message.api.controller.v1;

import java.util.Date;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.util.Results;
import org.hzero.message.domain.entity.MessageReceiver;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.message.api.vo.StackingHistogramVO;
import io.choerodon.message.app.service.MailingRecordService;
import io.choerodon.message.app.service.MessageC7nService;
import io.choerodon.message.infra.config.C7nSwaggerApiConfig;
import io.choerodon.message.infra.dto.MessageC7nDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

/**
 * 〈功能简述〉
 * 〈邮件发送记录相关操作Controller〉
 *
 * @author wanghao
 * @Date 2020/2/24 17:55
 */
@Api(tags = C7nSwaggerApiConfig.CHOERODON_MAIL_RECORD)
@RestController
@RequestMapping("/choerodon/v1/mails/records")
public class MailRecordController {

    private final MailingRecordService mailingRecordService;
    private final MessageC7nService messageC7nService;

    public MailRecordController(MailingRecordService mailingRecordService, MessageC7nService messageC7nService) {
        this.mailingRecordService = mailingRecordService;
        this.messageC7nService = messageC7nService;
    }

    @Permission(level = ResourceLevel.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    @GetMapping("/count_by_date")
    @ApiOperation(value = "按时间段统计邮件发送成功、失败次数(堆叠柱状图使用)")
    public ResponseEntity<StackingHistogramVO> countByDate(@RequestParam(value = "start_time") Date startTime,
                                                           @RequestParam(value = "end_time") Date endTime) {
        return ResponseEntity.ok(mailingRecordService.countByDate(startTime, endTime));
    }

    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/emails")
    @ApiOperation(value = "查询邮件消息记录（分页接口）")
    @CustomPageRequest
    public ResponseEntity<Page<MessageC7nDTO>> pageEmail(@ApiIgnore
                                                         @SortDefault(value = "message_id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                         @RequestParam(required = false) String status,
                                                         @RequestParam(required = false) String messageName,
                                                         @RequestParam(required = false) String params) {
        return new ResponseEntity<>(messageC7nService.listMessage(status, messageName, params, pageRequest), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/webhooks")
    @ApiOperation(value = "查询webhook消息记录（分页接口）")
    @CustomPageRequest
    public ResponseEntity<Page<MessageC7nDTO>> pageWebHooks(@ApiIgnore
                                                            @SortDefault(value = "message_id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                            @RequestParam(required = false) String status,
                                                            @RequestParam(required = false) String webhookAddress,
                                                            @RequestParam(required = false) String messageName,
                                                            @RequestParam(required = false) String failedReason,
                                                            @RequestParam(required = false) String params) {
        return new ResponseEntity<>(messageC7nService.listWebHooks(status, webhookAddress, messageName, failedReason, params, pageRequest), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/ding_talk/{organization_id}")
    @ApiOperation(value = "查询组织下钉钉消息记录（分页接口）")
    @CustomPageRequest
    public ResponseEntity<Page<MessageC7nDTO>> pageDingTalk(@PathVariable("organization_id") Long organizationId,
                                                            @ApiIgnore
                                                            @SortDefault(value = "message_id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                            @RequestParam(required = false) String status,
                                                            @RequestParam(required = false) String messageName,
                                                            @RequestParam(required = false) String params) {
        return new ResponseEntity<>(messageC7nService.pageDingTalk(organizationId, status, messageName, params, pageRequest), HttpStatus.OK);
    }

    @ApiOperation("查询消息接收人列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/ding_talk/{organization_id}/messages/{messageId}/receivers")
    @CustomPageRequest
    public ResponseEntity<Page<MessageReceiver>> listMessageReceiver(@PathVariable("organization_id") Long organizationId,
                                                                     @Encrypt @PathVariable long messageId,
                                                                     @ApiIgnore PageRequest pageRequest) {
        return Results.success(messageC7nService.listMessageReceiver(organizationId, messageId, pageRequest));
    }

}
