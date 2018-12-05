package io.choerodon.notify.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.notify.api.dto.SystemAnnouncementDTO;
import io.choerodon.notify.api.service.SystemAnnouncementService;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author dengyouquan
 **/
@RestController
@RequestMapping(value = "/v1/announcements")
public class SystemAnnouncementController {
    private SystemAnnouncementService systemAnnouncementService;

    public SystemAnnouncementController(SystemAnnouncementService systemAnnouncementService) {
        this.systemAnnouncementService = systemAnnouncementService;
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "新增系统公告")
    @PostMapping
    public ResponseEntity<SystemAnnouncementDTO> create(@RequestBody @Validated SystemAnnouncementDTO dto) {
        return new ResponseEntity<>(systemAnnouncementService.create(dto), HttpStatus.OK);
    }

    @Permission(permissionLogin = true)
    @ApiOperation(value = "分页查询全部系统公告")
    @CustomPageRequest
    @GetMapping
    public ResponseEntity<Page<SystemAnnouncementDTO>> pagingQuery(@ApiIgnore
                                                                   @SortDefault(value = "send_date", direction = Sort.Direction.DESC)
                                                                           PageRequest pageRequest,
                                                                   @RequestParam(required = false) String title,
                                                                   @RequestParam(required = false) String content,
                                                                   @RequestParam(required = false) String param) {
        return new ResponseEntity<>(systemAnnouncementService.pagingQuery(pageRequest, title, content, param), HttpStatus.OK);
    }
}
