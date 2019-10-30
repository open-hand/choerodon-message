package io.choerodon.notify.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.notify.api.dto.SiteMsgRecordDTO;
import io.choerodon.notify.api.service.SiteMsgRecordService;
import io.choerodon.notify.api.validator.SiteMsgRecordValidator;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @ApiOperation(value = "全局层查询用户站内信消息接口")
    @CustomPageRequest
    public ResponseEntity<PageInfo<SiteMsgRecordDTO>> pagingQuery(@ApiIgnore
                                                                  @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                                                  @RequestParam("user_id") Long userId,
                                                                  @RequestParam(value = "read", required = false) Boolean isRead,
                                                                  @RequestParam(required = false,value = "backlog_flag") Boolean backlogFlag
    ) {
        SiteMsgRecordValidator.validateCurrentUser(userId);
        return new ResponseEntity<>(siteMsgRecordService.pagingQueryByUserId(userId, isRead, backlogFlag, pageable.getPageNumber(), pageable.getPageSize()), HttpStatus.OK);
    }

    @PutMapping("/batch_read")
    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @ApiOperation(value = "全局层批量已读站内信消息接口")
    public ResponseEntity<List<SiteMsgRecordDTO>> batchRead(@RequestParam("user_id") Long userId,
                                                            @RequestBody Long[] ids) {
        SiteMsgRecordValidator.validateCurrentUser(userId);
        siteMsgRecordService.batchUpdateSiteMsgRecordIsRead(userId, ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/batch_delete")
    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @ApiOperation(value = "全局层批量删除站内信消息接口")
    public ResponseEntity<List<SiteMsgRecordDTO>> batchDeleted(@RequestParam("user_id") Long userId,
                                                               @RequestBody Long[] ids) {
        SiteMsgRecordValidator.validateCurrentUser(userId);
        siteMsgRecordService.batchUpdateSiteMsgRecordIsDeleted(userId, ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
