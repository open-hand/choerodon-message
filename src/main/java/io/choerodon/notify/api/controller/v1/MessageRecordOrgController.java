package io.choerodon.notify.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.constant.PageConstant;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.notify.api.dto.RecordListDTO;
import io.choerodon.notify.api.pojo.RecordQueryParam;
import io.choerodon.notify.api.service.MessageRecordService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static io.choerodon.notify.infra.config.NotifyProperties.LEVEL_ORG;


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
    public ResponseEntity<PageInfo<RecordListDTO>> pageEmail(@PathVariable("organization_id") long id,
                                                             @RequestParam(defaultValue = PageConstant.PAGE, required = false) final int page,
                                                             @RequestParam(defaultValue = PageConstant.SIZE, required = false) final int size,
                                                             @RequestParam(required = false) String status,
                                                             @RequestParam(required = false) String receiveEmail,
                                                             @RequestParam(required = false) String templateType,
                                                             @RequestParam(required = false) String failedReason,
                                                             @RequestParam(required = false) Integer retryCount,
                                                             @RequestParam(required = false) String params) {
        final RecordQueryParam param = new RecordQueryParam(status, receiveEmail, templateType, retryCount, failedReason, params, LEVEL_ORG);
        return new ResponseEntity<>(messageRecordService.pageEmail(param,page,size), HttpStatus.OK);
    }

    @Permission(type = ResourceType.SITE)
    @GetMapping("/emails/{id}/retry/organizations/{organization_id}")
    @ApiOperation(value = "组织层重试发送邮件")
    public void manualRetrySendEmail(@PathVariable long id,
                                     @PathVariable("organization_id") long orgId) {
        messageRecordService.manualRetrySendEmail(id);
    }


}
