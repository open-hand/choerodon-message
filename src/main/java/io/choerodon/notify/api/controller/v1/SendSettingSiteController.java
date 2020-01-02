package io.choerodon.notify.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.notify.api.dto.MessageServiceVO;
import io.choerodon.notify.api.dto.MsgServiceTreeVO;
import io.choerodon.notify.api.dto.SendSettingDetailTreeDTO;
import io.choerodon.notify.api.dto.SendSettingVO;
import io.choerodon.notify.api.service.SendSettingService;
import io.choerodon.notify.infra.dto.SendSettingDTO;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@RequestMapping("v1/notices/send_settings")
@Api("全局层发送设置接口")
public class SendSettingSiteController {

    private SendSettingService sendSettingService;

    public SendSettingSiteController(SendSettingService sendSettingService) {
        this.sendSettingService = sendSettingService;
    }

    @GetMapping
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "全局层分页查询消息服务列表")
    @CustomPageRequest
    public ResponseEntity<PageInfo<MessageServiceVO>> pageSite(@ApiIgnore
                                                               @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                                               @RequestParam(required = false) String messageType,
                                                               @RequestParam(required = false) String introduce,
                                                               @RequestParam(required = false) Boolean enabled,
                                                               @RequestParam(required = false) Boolean allowConfig,
                                                               @RequestParam(required = false) String params,
                                                               @RequestParam(required = false) String firstCode,
                                                               @RequestParam(required = false) String secondCode) {
        return new ResponseEntity<>(sendSettingService.pagingAll(messageType, introduce, enabled, allowConfig, params, pageable, firstCode, secondCode), HttpStatus.OK);
    }

    @GetMapping("/tree")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "获取消息服务树形结构")
    public ResponseEntity<List<MsgServiceTreeVO>> getMsgServiceTree(@RequestParam(required = false) String name) {
        return new ResponseEntity<>(sendSettingService.getMsgServiceTree(), HttpStatus.OK);
    }

    @GetMapping("/detail")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "全局层查看发送设置详情")
    public ResponseEntity<SendSettingVO> query(@RequestParam String code) {
        return new ResponseEntity<>(sendSettingService.query(code), HttpStatus.OK);
    }

    @GetMapping("/list/allow_config")
    @Permission(permissionLogin = true)
    @ApiOperation(value = "根据层级查询未禁用通知配置的启用状态的发送设置列表")
    public ResponseEntity<List<SendSettingDetailTreeDTO>> listLevelAndAllowConfig(@RequestParam(name = "source_type") String level) {
        return new ResponseEntity<>(sendSettingService.queryByLevelAndAllowConfig(level, true), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    @ApiOperation(value = "根据Id删除发送设置")
    public ResponseEntity delSendSetting(@PathVariable Long id) {
        sendSettingService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PutMapping("/enabled")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "根据code启用消息服务")
    public ResponseEntity<MessageServiceVO> enabled(@RequestParam("code") String code) {
        return new ResponseEntity<>(sendSettingService.enabled(code), HttpStatus.OK);
    }

    @PutMapping("/disabled")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "根据code停用消息服务")
    public ResponseEntity<MessageServiceVO> disabled(@RequestParam("code") String code) {
        return new ResponseEntity<>(sendSettingService.disabled(code), HttpStatus.OK);
    }

    @PutMapping("/{id}/send_setting")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "修改发送设置")
    public ResponseEntity<SendSettingDTO> updateSendSetting(@PathVariable("id") Long id, @RequestBody SendSettingDTO updateDTO) {
        return ResponseEntity.ok(sendSettingService.updateSendSetting(id, updateDTO));
    }

    @PutMapping("/{id}/allow_configuration")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "允许配置接受设置")
    public ResponseEntity<MessageServiceVO> allowConfig(@PathVariable("id") Long id) {
        return new ResponseEntity<>(sendSettingService.allowConfiguration(id), HttpStatus.OK);
    }

    @PutMapping("/{id}/forbidden_configuration")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "禁止配置接受设置")
    public ResponseEntity<MessageServiceVO> forbiddenConfig(@PathVariable("id") Long id) {
        return new ResponseEntity<>(sendSettingService.forbiddenConfiguration(id), HttpStatus.OK);
    }
    @GetMapping("/codes/{code}")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "根据code查询发送设置")
    public ResponseEntity<SendSettingDTO> queryByCode(@PathVariable("code") String code) {
        return new ResponseEntity<>(sendSettingService.queryByCode(code), HttpStatus.OK);
    }

}