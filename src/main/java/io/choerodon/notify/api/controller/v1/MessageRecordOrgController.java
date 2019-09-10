package io.choerodon.notify.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.base.annotation.Permission;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.base.domain.Sort;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.annotation.SortDefault;
import io.choerodon.notify.api.dto.RecordListDTO;
import io.choerodon.notify.api.service.MessageRecordService;
import io.choerodon.notify.api.vo.MessageRecordSearchVO;
import io.choerodon.swagger.annotation.CustomPageRequest;


@RestController
@RequestMapping("v1/records")
public class MessageRecordOrgController {

    private MessageRecordService messageRecordService;

    public MessageRecordOrgController(MessageRecordService messageRecordService) {
        this.messageRecordService = messageRecordService;
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @PostMapping("/emails/list/organizations/{organization_id}")
    @ApiOperation(value = "组织层分页查询邮件消息记录")
    @CustomPageRequest
    public ResponseEntity<PageInfo<RecordListDTO>> pageEmail(@PathVariable("organization_id") long id,
                                                             @ApiIgnore
                                                             @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                             @RequestBody MessageRecordSearchVO messageRecordSearchVO) {
        messageRecordSearchVO.setLevel(ResourceLevel.ORGANIZATION.value());
        return new ResponseEntity<>(messageRecordService.pageEmail(pageRequest, messageRecordSearchVO), HttpStatus.OK);
    }

    @Permission(type = ResourceType.SITE)
    @GetMapping("/emails/{id}/retry/organizations/{organization_id}")
    @ApiOperation(value = "组织层重试发送邮件")
    public void manualRetrySendEmail(@PathVariable long id,
                                     @PathVariable("organization_id") long orgId) {
        messageRecordService.manualRetrySendEmail(id);
    }


}
