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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("v1/records")
public class MessageRecordSiteController {

    private MessageRecordService messageRecordService;

    public MessageRecordSiteController(MessageRecordService messageRecordService) {
        this.messageRecordService = messageRecordService;
    }


    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/email")
    @ApiOperation(value = "全局层分页查询邮件消息记录")
    @CustomPageRequest
    public ResponseEntity<Page<RecordListDTO>> pageEmail(@ApiIgnore @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                         @RequestParam(required = false) String status,
                                                         @RequestParam(required = false) String receiveEmail,
                                                         @RequestParam(required = false) String templateType,
                                                         @RequestParam(required = false) String failedReason,
                                                         @RequestParam(required = false) String params) {
        return new ResponseEntity<>(messageRecordService.pageEmail(pageRequest, status, receiveEmail,
                templateType, failedReason, params), HttpStatus.OK);
    }

}
