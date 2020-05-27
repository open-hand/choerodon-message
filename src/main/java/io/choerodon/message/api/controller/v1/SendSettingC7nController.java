package io.choerodon.message.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.message.api.vo.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.util.Results;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.message.app.service.SendSettingC7nService;
import io.choerodon.message.infra.config.C7nSwaggerApiConfig;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

import java.util.List;

@Api(tags = C7nSwaggerApiConfig.CHOERODON_SEND_SETTING)
@RestController
@RequestMapping("/choerodon/v1/notices/send_settings")
public class SendSettingC7nController {

    private SendSettingC7nService sendSettingC7nService;

    public SendSettingC7nController(SendSettingC7nService sendSettingC7nService) {
        this.sendSettingC7nService = sendSettingC7nService;
    }

    @GetMapping
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "全局层分页查询消息服务列表")
    @CustomPageRequest
    public ResponseEntity<Page<MessageServiceVO>> pageSite(@ApiIgnore
                                                           @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                           @RequestParam(required = false) String messageCode,
                                                           @RequestParam(required = false) String messageName,
                                                           @RequestParam(required = false) Boolean enabled,
                                                           @RequestParam(required = false) Boolean receiveConfigFlag,
                                                           @RequestParam(required = false) String params,
                                                           @RequestParam(required = false) String firstCode,
                                                           @RequestParam(required = false) String secondCode) {
        return new ResponseEntity<>(sendSettingC7nService.pagingAll(messageCode, messageName, enabled, receiveConfigFlag, params, pageRequest, firstCode, secondCode), HttpStatus.OK);
    }

    @GetMapping("/tree")
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "获取消息服务树形结构")
    public ResponseEntity<List<MsgServiceTreeVO>> getMsgServiceTree() {
        return new ResponseEntity<>(sendSettingC7nService.getMsgServiceTree(), HttpStatus.OK);
    }

    @GetMapping("/detail")
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "全局层查看发送设置详情")
    public ResponseEntity<SendSettingVO> query(@RequestParam String tempServerCode) {
        return new ResponseEntity<>(sendSettingC7nService.queryByTempServerCode(tempServerCode), HttpStatus.OK);
    }

    @GetMapping("/list/allow_config")
    @Permission(permissionLogin = true)
    @ApiOperation(value = "根据层级查询未禁用通知配置的启用状态的发送设置列表")
    public ResponseEntity<List<SendSettingDetailTreeVO>> listLevelAndAllowConfig(@RequestParam(name = "source_type") String level) {
        return new ResponseEntity<>(sendSettingC7nService.queryByLevelAndAllowConfig(level, 1), HttpStatus.OK);
    }


    @PutMapping("/update_status")
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "根据code启用消息服务")
    public ResponseEntity updateStatus(@RequestParam("code") String code, @RequestParam(value = "status") Boolean status) {
        sendSettingC7nService.enableOrDisabled(code, status);
        return Results.success();
    }


    @PutMapping("/{id}/send_setting")
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "修改发送设置")
    public ResponseEntity<SendSettingVO> updateSendSetting(@PathVariable("id") Long id, @RequestBody SendSettingVO settingVO) {
        return ResponseEntity.ok(sendSettingC7nService.updateSendSetting(id, settingVO));
    }

    @PutMapping("/{id}/receive_config_flag")
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "允许配置接受设置")
    public ResponseEntity updateReceiveConfigFlag(@PathVariable("id") Long id, Boolean receiveConfigFlag) {
        sendSettingC7nService.updateReceiveConfigFlag(id, receiveConfigFlag);
        return Results.success();
    }


    @GetMapping("/codes/{code}")
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "根据code查询发送设置")
    public ResponseEntity<SendSettingVO> queryByCode(@PathVariable("code") String code) {
        return new ResponseEntity<>(sendSettingC7nService.queryByCode(code), HttpStatus.OK);
    }

    @GetMapping("/codes/resourceDeleteConfirmation/check_enabled")
    @Permission(level = ResourceLevel.SITE, permissionLogin = true)
    @ApiOperation(value = "检查资源删除验证通知是否启用")
    public ResponseEntity<Boolean> checkResourceDeleteEnabled() {
        return ResponseEntity.ok(sendSettingC7nService.checkResourceDeleteEnabled());
    }


    @ApiOperation("创建消息模板")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/template")
    public ResponseEntity<MessageTemplateVO> createMessageTemplate(@RequestBody MessageTemplateVO messageTemplateVO) {
        return Results.created(sendSettingC7nService.createMessageTemplate(messageTemplateVO));
    }
}