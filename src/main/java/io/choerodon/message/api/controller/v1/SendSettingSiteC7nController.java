package io.choerodon.message.api.controller.v1;

import io.choerodon.message.infra.dto.SendSettingDetailTreeDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.message.api.vo.SendSettingVO;
import io.choerodon.message.app.service.SendSettingC7nService;
import io.choerodon.swagger.annotation.Permission;

import java.util.List;

@RestController
@RequestMapping("v1/notices/send_settings")
@Api("全局层发送设置接口")
public class SendSettingSiteC7nController {

    private SendSettingC7nService sendSettingC7nService;

    public SendSettingSiteC7nController(SendSettingC7nService sendSettingC7nService) {
        this.sendSettingC7nService = sendSettingC7nService;
    }
//
//    @GetMapping
//    @Permission(type = ResourceType.SITE)
//    @ApiOperation(value = "全局层分页查询消息服务列表")
//    @CustomPageRequest
//    public ResponseEntity<PageInfo<MessageServiceVO>> pageSite(@ApiIgnore
//                                                               @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable pageable,
//                                                               @RequestParam(required = false) String messageType,
//                                                               @RequestParam(required = false) String introduce,
//                                                               @RequestParam(required = false) Boolean enabled,
//                                                               @RequestParam(required = false) Boolean allowConfig,
//                                                               @RequestParam(required = false) String params,
//                                                               @RequestParam(required = false) String firstCode,
//                                                               @RequestParam(required = false) String secondCode) {
//        return new ResponseEntity<>(sendSettingC7nService.pagingAll(messageType, introduce, enabled, allowConfig, params, pageable, firstCode, secondCode), HttpStatus.OK);
//    }
//
//    @GetMapping("/tree")
//    @Permission(type = ResourceType.SITE)
//    @ApiOperation(value = "获取消息服务树形结构")
//    public ResponseEntity<List<MsgServiceTreeVO>> getMsgServiceTree(@RequestParam(required = false) String name) {
//        return new ResponseEntity<>(sendSettingC7nService.getMsgServiceTree(), HttpStatus.OK);
//    }

    @GetMapping("/detail")
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "全局层查看发送设置详情")
    public ResponseEntity<SendSettingVO> query(@RequestParam Long tempServerId) {
        return new ResponseEntity<>(sendSettingC7nService.queryByTempServerId(tempServerId), HttpStatus.OK);
    }

    @GetMapping("/list/allow_config")
    @Permission(permissionLogin = true)
    @ApiOperation(value = "根据层级查询未禁用通知配置的启用状态的发送设置列表")
    public ResponseEntity<List<SendSettingDetailTreeDTO>> listLevelAndAllowConfig(@RequestParam(name = "source_type") String level) {
        return new ResponseEntity<>(sendSettingC7nService.queryByLevelAndAllowConfig(level, true), HttpStatus.OK);
    }
//
//    @DeleteMapping("/{id}")
//    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
//    @ApiOperation(value = "根据Id删除发送设置")
//    public ResponseEntity delSendSetting(@PathVariable Long id) {
//        sendSettingC7nService.delete(id);
//        return new ResponseEntity(HttpStatus.OK);
//    }
//
//    @PutMapping("/enabled")
//    @Permission(type = ResourceType.SITE)
//    @ApiOperation(value = "根据code启用消息服务")
//    public ResponseEntity<MessageServiceVO> enabled(@RequestParam("code") String code) {
//        return new ResponseEntity<>(sendSettingC7nService.enabled(code), HttpStatus.OK);
//    }
//
//    @PutMapping("/disabled")
//    @Permission(type = ResourceType.SITE)
//    @ApiOperation(value = "根据code停用消息服务")
//    public ResponseEntity<MessageServiceVO> disabled(@RequestParam("code") String code) {
//        return new ResponseEntity<>(sendSettingC7nService.disabled(code), HttpStatus.OK);
//    }
//
//    @PutMapping("/{id}/send_setting")
//    @Permission(type = ResourceType.SITE)
//    @ApiOperation(value = "修改发送设置")
//    public ResponseEntity<SendSettingDTO> updateSendSetting(@PathVariable("id") Long id, @RequestBody SendSettingDTO updateDTO) {
//        return ResponseEntity.ok(sendSettingC7nService.updateSendSetting(id, updateDTO));
//    }
//
//    @PutMapping("/{id}/allow_configuration")
//    @Permission(type = ResourceType.SITE)
//    @ApiOperation(value = "允许配置接受设置")
//    public ResponseEntity<MessageServiceVO> allowConfig(@PathVariable("id") Long id) {
//        return new ResponseEntity<>(sendSettingC7nService.allowConfiguration(id), HttpStatus.OK);
//    }
//
//    @PutMapping("/{id}/forbidden_configuration")
//    @Permission(type = ResourceType.SITE)
//    @ApiOperation(value = "禁止配置接受设置")
//    public ResponseEntity<MessageServiceVO> forbiddenConfig(@PathVariable("id") Long id) {
//        return new ResponseEntity<>(sendSettingC7nService.forbiddenConfiguration(id), HttpStatus.OK);
//    }
//    @GetMapping("/codes/{code}")
//    @Permission(type = ResourceType.SITE)
//    @ApiOperation(value = "根据code查询发送设置")
//    public ResponseEntity<SendSettingDTO> queryByTempServerId(@PathVariable("code") String code) {
//        return new ResponseEntity<>(sendSettingC7nService.queryByTempServerId(code), HttpStatus.OK);
//    }
//    @GetMapping("/codes/resourceDeleteConfirmation/check_enabled")
//    @Permission(type = ResourceType.SITE, permissionLogin = true)
//    @ApiOperation(value = "检查资源删除验证通知是否启用")
//    public ResponseEntity<Boolean> checkResourceDeleteEnabled() {
//        return ResponseEntity.ok(sendSettingC7nService.checkResourceDeleteEnabled());
//    }
}