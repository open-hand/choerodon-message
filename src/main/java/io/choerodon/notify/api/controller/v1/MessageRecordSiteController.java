package io.choerodon.notify.api.controller.v1;

import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.notify.api.dto.RecordListDTO;
import io.choerodon.notify.api.pojo.RecordQueryParam;
import io.choerodon.notify.api.service.MessageRecordService;
import io.choerodon.notify.domain.Record;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;


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
    public ResponseEntity<Page<RecordListDTO>> pageEmail(@ApiIgnore @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                         @RequestParam(required = false) String status,
                                                         @RequestParam(required = false) String receiveEmail,
                                                         @RequestParam(required = false) String templateType,
                                                         @RequestParam(required = false) Integer retryStatus,
                                                         @RequestParam(required = false) String failedReason,
                                                         @RequestParam(required = false) String params) {
        final RecordQueryParam param = new RecordQueryParam(status, receiveEmail, templateType, retryStatus, failedReason, params, null);
        param.setPageRequest(pageRequest);
        return new ResponseEntity<>(messageRecordService.pageEmail(param), HttpStatus.OK);
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping("/emails/{id}/retry")
    @ApiOperation(value = "全局层重试发送邮件")
    public Record manualRetrySendEmail(@PathVariable long id) {
        return messageRecordService.manualRetrySendEmail(id);
    }

}
