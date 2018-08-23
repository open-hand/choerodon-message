package io.choerodon.notify.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.notify.api.dto.RecordListDTO;
import io.choerodon.notify.api.service.MessageRecordService;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import static io.choerodon.notify.infra.config.NotifyProperties.LEVEL_SITE;

@RestController
@RequestMapping("v1/records")
public class MessageRecordSiteController {

    private MessageRecordService messageRecordService;

    public MessageRecordSiteController(MessageRecordService messageRecordService) {
        this.messageRecordService = messageRecordService;
    }


    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/emails")
    @ApiOperation(value = "全局层分页查询邮件消息记录")
    @CustomPageRequest
    public ResponseEntity<Page<RecordListDTO>> pageEmail(@ApiIgnore @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                         @RequestParam(required = false) String status,
                                                         @RequestParam(required = false) String receiveEmail,
                                                         @RequestParam(required = false) String templateType,
                                                         @RequestParam(required = false) String failedReason,
                                                         @RequestParam(required = false) String params) {
        return new ResponseEntity<>(messageRecordService.pageEmail(pageRequest, status, receiveEmail,
                templateType, failedReason, params, LEVEL_SITE), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/emails/{id}/retry")
    @ApiOperation(value = "全局层重试发送邮件")
    public void manualRetrySendEmail(@PathVariable long id) {
        messageRecordService.manualRetrySendEmail(id);
    }

}
