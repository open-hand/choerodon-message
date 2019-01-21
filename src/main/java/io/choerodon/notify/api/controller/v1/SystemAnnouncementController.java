package io.choerodon.notify.api.controller.v1;

import java.util.Date;
import javax.validation.Valid;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.notify.api.dto.SystemAnnouncementDTO;
import io.choerodon.notify.api.service.SystemAnnouncementService;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

/**
 * @author dengyouquan, Eugen
 **/
@RestController
@RequestMapping(value = "/v1/system_notice")
public class SystemAnnouncementController {
    private SystemAnnouncementService systemAnnouncementService;

    public SystemAnnouncementController(SystemAnnouncementService systemAnnouncementService) {
        this.systemAnnouncementService = systemAnnouncementService;
    }

    public void setSystemAnnouncementService(SystemAnnouncementService systemAnnouncementService) {
        this.systemAnnouncementService = systemAnnouncementService;
    }

    @Permission(level = ResourceLevel.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    @ApiOperation(value = "新增系统公告")
    @PostMapping("/create")
    public ResponseEntity<SystemAnnouncementDTO> create(@RequestBody @Valid SystemAnnouncementDTO dto) {
        dto.setStatus(SystemAnnouncementDTO.AnnouncementStatus.WAITING.value());
        if (dto.getSendDate().getTime() < System.currentTimeMillis()) {
            throw new CommonException("error.create.system.announcement.sendDate.cant.before.now");
        }
        if (dto.getSendDate() == null) {
            dto.setSendDate(new Date());
        }
        if(dto.getSticky()!=null && dto.getSticky() && dto.getEndDate()==null){
            throw new CommonException("error.create.system.announcement.endDate.is.null");
        }
        return new ResponseEntity<>(systemAnnouncementService.create(dto), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    @ApiOperation(value = "分页查询系统公告")
    @CustomPageRequest
    @GetMapping("/all")
    public ResponseEntity<Page<SystemAnnouncementDTO>> pagingQuery(@ApiIgnore
                                                                   @SortDefault(value = "send_date", direction = Sort.Direction.DESC)
                                                                           PageRequest pageRequest,
                                                                   @RequestParam(required = false) String title,
                                                                   @RequestParam(required = false) String content,
                                                                   @RequestParam(required = false) String status,
                                                                   @RequestParam(required = false) Boolean sendNotices,
                                                                   @RequestParam(required = false) String param) {
        return new ResponseEntity<>(systemAnnouncementService.pagingQuery(pageRequest, title, content, param, status, sendNotices), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.SITE, permissionLogin = true)
    @ApiOperation(value = "分页查询已发送的系统公告")
    @CustomPageRequest
    @GetMapping("/completed")
    public ResponseEntity<Page<SystemAnnouncementDTO>> pagingQueryCompleted(@ApiIgnore
                                                                   @SortDefault(value = "send_date", direction = Sort.Direction.DESC)
                                                                           PageRequest pageRequest) {
        return new ResponseEntity<>(systemAnnouncementService.pagingQuery(pageRequest, null, null, null,
                SystemAnnouncementDTO.AnnouncementStatus.COMPLETED.value(), null), HttpStatus.OK);
    }


    @Permission(level = ResourceLevel.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    @ApiOperation(value = "查看系统公告详情")
    @GetMapping("/{id}")
    public ResponseEntity<SystemAnnouncementDTO> getDetail(@PathVariable("id") Long id) {
        return new ResponseEntity<>(systemAnnouncementService.getDetailById(id), HttpStatus.OK);
    }


    @Permission(level = ResourceLevel.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    @ApiOperation(value = "删除系统公告")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        systemAnnouncementService.delete(id);
    }

    @Permission(level = ResourceLevel.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    @ApiOperation(value = "更新系统公告")
    @PutMapping("/update")
    public ResponseEntity<SystemAnnouncementDTO> update(@RequestBody @Validated SystemAnnouncementDTO dto) {
        return new ResponseEntity<>(systemAnnouncementService.update(dto, ResourceLevel.SITE, 0L), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.SITE, permissionLogin = true)
    @ApiOperation(value = "查询当前需悬浮显示的最新公告")
    @GetMapping("/new_sticky")
    public ResponseEntity<SystemAnnouncementDTO> getNewSticky() {
        return new ResponseEntity<>(systemAnnouncementService.getLatestSticky(), HttpStatus.OK);
    }
}
