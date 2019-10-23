package io.choerodon.notify.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.base.annotation.*;
import io.choerodon.base.enums.*;
import io.choerodon.notify.api.dto.*;
import io.choerodon.notify.api.service.*;
import io.choerodon.notify.domain.*;
import io.choerodon.swagger.annotation.*;


@RestController
@RequestMapping("v1/records")
public class MessageRecordSiteController {

    private MessageRecordService messageRecordService;

    public MessageRecordSiteController(MessageRecordService messageRecordService) {
        this.messageRecordService = messageRecordService;
    }

    @Permission(type = ResourceType.SITE)
    @GetMapping("/emails")
    @ApiOperation(value = "全局层分页查询邮件消息记录")
    @CustomPageRequest
    public ResponseEntity<PageInfo<RecordListDTO>> pageEmail(@SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                                             @RequestParam(required = false) String status,
                                                             @RequestParam(required = false) String receiveEmail,
                                                             @RequestParam(required = false) String templateType,
                                                             @RequestParam(required = false) String failedReason,
                                                             @RequestParam(required = false) String params) {
        return new ResponseEntity<>(messageRecordService.pageEmail(status, receiveEmail, templateType, failedReason, params, pageable, null), HttpStatus.OK);
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping("/emails/{id}/retry")
    @ApiOperation(value = "全局层重试发送邮件")
    public Record manualRetrySendEmail(@PathVariable long id) {
        return messageRecordService.manualRetrySendEmail(id);
    }

}
