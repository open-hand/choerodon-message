package io.choerodon.notify.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.annotation.Permission;;
import io.choerodon.core.enums.ResourceType;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.iam.*;
import io.choerodon.notify.api.dto.*;
import io.choerodon.notify.api.service.*;
import io.choerodon.swagger.annotation.*;


@RestController
@RequestMapping("v1/records")
public class MessageRecordOrgController {

    private MessageRecordService messageRecordService;

    public MessageRecordOrgController(MessageRecordService messageRecordService) {
        this.messageRecordService = messageRecordService;
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @GetMapping("/emails/organizations/{organization_id}")
    @ApiOperation(value = "组织层分页查询邮件消息记录")
    @CustomPageRequest
    public ResponseEntity<PageInfo<RecordListDTO>> pageEmail(@PathVariable("organization_id") long id,
                                                             @ApiIgnore
                                                             @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                                             @RequestParam(required = false) String status,
                                                             @RequestParam(required = false) String receiveEmail,
                                                             @RequestParam(required = false) String templateType,
                                                             @RequestParam(required = false) String failedReason,
                                                             @RequestParam(required = false) String params) {
        return new ResponseEntity<>(messageRecordService.pageEmail(status, receiveEmail, templateType, failedReason, params, pageable, ResourceLevel.ORGANIZATION.value()), HttpStatus.OK);
    }

    @Permission(type = ResourceType.SITE)
    @GetMapping("/emails/{id}/retry/organizations/{organization_id}")
    @ApiOperation(value = "组织层重试发送邮件")
    public void manualRetrySendEmail(@PathVariable long id,
                                     @PathVariable("organization_id") long orgId) {
        messageRecordService.manualRetrySendEmail(id);
    }


}
