package io.choerodon.notify.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.notify.api.dto.RecordListDTO;
import io.choerodon.notify.api.pojo.RecordQueryParam;
import io.choerodon.notify.api.service.MessageRecordService;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import static io.choerodon.notify.infra.config.NotifyProperties.LEVEL_ORG;


@RestController
@RequestMapping("v1/records")
public class MessageRecordOrgController {

    private MessageRecordService messageRecordService;

    public MessageRecordOrgController(MessageRecordService messageRecordService) {
        this.messageRecordService = messageRecordService;
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/emails/organizations/{organization_id}")
    @ApiOperation(value = "组织层分页查询邮件消息记录")
    @CustomPageRequest
    public ResponseEntity<Page<RecordListDTO>> pageEmail(@PathVariable("organization_id") long id,
                                                         @ApiIgnore @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                         @RequestParam(required = false) String status,
                                                         @RequestParam(required = false) String receiveEmail,
                                                         @RequestParam(required = false) String templateType,
                                                         @RequestParam(required = false) String failedReason,
                                                         @RequestParam(required = false) String retryStatus,
                                                         @RequestParam(required = false) String params) {
        final RecordQueryParam param = new RecordQueryParam(status, receiveEmail, templateType, retryStatus, failedReason, params, LEVEL_ORG);
        param.setPageRequest(pageRequest);
        return new ResponseEntity<>(messageRecordService.pageEmail(param), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/emails/{id}/retry/organizations/{organization_id}")
    @ApiOperation(value = "组织层重试发送邮件")
    public void manualRetrySendEmail(@PathVariable long id,
                                     @PathVariable("organization_id") long orgId) {
        messageRecordService.manualRetrySendEmail(id);
    }


}
