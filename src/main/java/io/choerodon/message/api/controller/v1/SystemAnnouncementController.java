package io.choerodon.message.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.message.api.vo.SystemAnnouncementVO;
import io.choerodon.message.app.service.SystemAnnouncementService;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Date;

/**
 * @author dengyouquan, Eugen
 **/
@RestController
@RequestMapping(value = "/choerodon/v1/system_notice")
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
    public ResponseEntity<SystemAnnouncementVO> create(@RequestBody @Valid SystemAnnouncementVO dto) {
        dto.setStatus(SystemAnnouncementVO.AnnouncementStatus.WAITING.value());
        if (dto.getSendDate().getTime() < System.currentTimeMillis()) {
            throw new CommonException("error.create.system.announcement.sendDate.cant.before.now");
        }
        if (dto.getSendDate() == null) {
            dto.setSendDate(new Date());
        }
        if (dto.getSticky() != null && dto.getSticky() && dto.getEndDate() == null) {
            throw new CommonException("error.create.system.announcement.endDate.is.null");
        }
        return new ResponseEntity<>(systemAnnouncementService.create(dto), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    @ApiOperation(value = "查询系统公告（分页接口）")
    @GetMapping("/all")
    public ResponseEntity<Page<SystemAnnouncementVO>> pagingQuery(@SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                                  @RequestParam(required = false) String title,
                                                                  @RequestParam(required = false) String status,
                                                                  @RequestParam(required = false) String params) {
        return new ResponseEntity<>(systemAnnouncementService.pagingQuery(pageRequest, title, status, params), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.SITE, permissionLogin = true)
    @ApiOperation(value = "查询已发送的系统公告（分页接口）")
    @GetMapping("/completed")
    public ResponseEntity<Page<SystemAnnouncementVO>> pagingQueryCompleted(@SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return new ResponseEntity<>(systemAnnouncementService.pagingQuery(pageRequest, null,
                SystemAnnouncementVO.AnnouncementStatus.PUBLISHED.value(), null), HttpStatus.OK);
    }

//    @Permission(level = ResourceLevel.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
//    @ApiOperation(value = "查看系统公告详情")
//    @GetMapping("/{id}")
//    public ResponseEntity<SystemAnnouncementVO> getDetail(@PathVariable("id") Long id) {
//        return new ResponseEntity<>(systemAnnouncementService.getDetailById(id), HttpStatus.OK);
//    }
//
//
//    @Permission(level = ResourceLevel.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
//    @ApiOperation(value = "删除系统公告")
//    @DeleteMapping("/{id}")
//    public void delete(@PathVariable long id) {
//        systemAnnouncementService.delete(id);
//    }
//
//    @Permission(level = ResourceLevel.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
//    @ApiOperation(value = "更新系统公告")
//    @PutMapping("/update")
//    public ResponseEntity<SystemAnnouncementVO> update(@RequestBody @Validated SystemAnnouncementVO dto) {
//        return new ResponseEntity<>(systemAnnouncementService.update(dto, ResourceLevel.SITE, 0L), HttpStatus.OK);
//    }
//
//    @Permission(level = ResourceLevel.SITE, permissionLogin = true)
//    @ApiOperation(value = "查询当前需悬浮显示的最新公告")
//    @GetMapping("/new_sticky")
//    public ResponseEntity<SystemAnnouncementVO> getNewSticky() {
//        return new ResponseEntity<>(systemAnnouncementService.getLatestSticky(), HttpStatus.OK);
//    }
}
