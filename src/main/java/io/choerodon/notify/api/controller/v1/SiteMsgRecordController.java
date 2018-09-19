package io.choerodon.notify.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.notify.api.dto.SiteMsgRecordDTO;
import io.choerodon.notify.api.service.SiteMsgRecordService;
import io.choerodon.notify.api.validator.SiteMsgRecordValidator;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@RequestMapping("v1/notices/sitemsgs")
@Api("站内信历史消息接口")
public class SiteMsgRecordController {

    private SiteMsgRecordService siteMsgRecordService;

    public SiteMsgRecordController(SiteMsgRecordService siteMsgRecordService) {
        this.siteMsgRecordService = siteMsgRecordService;
    }

    @GetMapping
    @Permission(level = ResourceLevel.SITE, permissionPublic = true)
    @CustomPageRequest
    @ApiOperation(value = "全局层查询用户站内信消息接口")
    public ResponseEntity<Page<SiteMsgRecordDTO>> pagingQuery(@ApiIgnore @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                              @RequestParam("user_id") Long userId,
                                                              @RequestParam(value = "read", required = false) Boolean isRead
    ) {
        SiteMsgRecordValidator.validateCurrentUser(userId);
        return new ResponseEntity<>(siteMsgRecordService.pagingQueryByUserId(userId, isRead, pageRequest), HttpStatus.OK);
    }

    @PutMapping("/batch_read")
    @Permission(level = ResourceLevel.SITE, permissionPublic = true)
    @ApiOperation(value = "全局层批量已读站内信消息接口")
    public ResponseEntity<List<SiteMsgRecordDTO>> batchRead(@RequestParam("user_id") Long userId,
                                                            @RequestBody Long[] ids) {
        SiteMsgRecordValidator.validateCurrentUser(userId);
        siteMsgRecordService.batchUpdateSiteMsgRecordIsRead(userId, ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/batch_delete")
    @Permission(level = ResourceLevel.SITE, permissionPublic = true)
    @ApiOperation(value = "全局层批量删除站内信消息接口")
    public ResponseEntity<List<SiteMsgRecordDTO>> batchDeleted(@RequestParam("user_id") Long userId,
                                                               @RequestBody Long[] ids) {
        SiteMsgRecordValidator.validateCurrentUser(userId);
        siteMsgRecordService.batchUpdateSiteMsgRecordIsDeleted(userId, ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
