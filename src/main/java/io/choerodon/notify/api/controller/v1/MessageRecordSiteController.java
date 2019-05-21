package io.choerodon.notify.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.constant.PageConstant;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.notify.api.dto.RecordListDTO;
import io.choerodon.notify.api.pojo.RecordQueryParam;
import io.choerodon.notify.api.service.MessageRecordService;
import io.choerodon.notify.domain.Record;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
    public ResponseEntity<PageInfo<RecordListDTO>> pageEmail(@RequestParam(defaultValue = PageConstant.PAGE, required = false) final int page,
                                                             @RequestParam(defaultValue = PageConstant.SIZE, required = false) final int size,
                                                             @RequestParam(required = false) String status,
                                                             @RequestParam(required = false) String receiveEmail,
                                                             @RequestParam(required = false) String templateType,
                                                             @RequestParam(required = false) Integer retryStatus,
                                                             @RequestParam(required = false) String failedReason,
                                                             @RequestParam(required = false) String params) {
        final RecordQueryParam param = new RecordQueryParam(status, receiveEmail, templateType, retryStatus, failedReason, params, null);
        return new ResponseEntity<>(messageRecordService.pageEmail(param, page, size), HttpStatus.OK);
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping("/emails/{id}/retry")
    @ApiOperation(value = "全局层重试发送邮件")
    public Record manualRetrySendEmail(@PathVariable long id) {
        return messageRecordService.manualRetrySendEmail(id);
    }

}
