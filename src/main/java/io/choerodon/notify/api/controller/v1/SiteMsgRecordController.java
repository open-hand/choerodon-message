package io.choerodon.notify.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.notify.api.dto.SiteMsgRecordDTO;
import io.choerodon.notify.api.pojo.SiteMsgRecordQueryParam;
import io.choerodon.notify.api.service.SiteMsgRecordService;
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

    @GetMapping("/users/{id}/not_read")
    @Permission(level = ResourceLevel.SITE)
    @CustomPageRequest
    @ApiOperation(value = "全局层查询用户所有的站内信未读消息接口")
    public ResponseEntity<List<SiteMsgRecordDTO>> pagingQueryNotRead(@PathVariable(value = "id") Long userId) {
        return new ResponseEntity<>(siteMsgRecordService.listByReadAndId(userId,false), HttpStatus.OK);
    }

    @GetMapping("/users/{id}")
    @Permission(level = ResourceLevel.SITE)
    @CustomPageRequest
    @ApiOperation(value = "全局层查询用户站内信消息接口")
    public ResponseEntity<Page<SiteMsgRecordDTO>> pagingQuery(@PathVariable(value = "id") Long userId,
                                                              @ApiIgnore @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest) {
        SiteMsgRecordQueryParam queryParam = new SiteMsgRecordQueryParam(userId, false, pageRequest);
        return new ResponseEntity<>(siteMsgRecordService.pagingQuery(queryParam), HttpStatus.OK);
    }

    @PutMapping("/users/{id}/batch_read")
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "全局层批量已读站内信消息接口")
    public ResponseEntity<List<SiteMsgRecordDTO>> batchRead(@PathVariable(value = "id") long userId,
                                                            @RequestBody Long[] ids) {
        siteMsgRecordService.batchUpdateSiteMsgRecordIsRead(userId, ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/users/{id}/batch_delete")
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "全局层批量删除站内信消息接口")
    public ResponseEntity<List<SiteMsgRecordDTO>> batchDeleted(@PathVariable(value = "id") long userId,
                                                               @RequestBody Long[] ids) {
        siteMsgRecordService.batchUpdateSiteMsgRecordIsDeleted(userId, ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
