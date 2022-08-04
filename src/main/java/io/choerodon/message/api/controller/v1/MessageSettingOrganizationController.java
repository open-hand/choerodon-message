package io.choerodon.message.api.controller.v1;


import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.message.api.vo.CustomMessageSettingVO;
import io.choerodon.message.api.vo.MessageSettingVO;
import io.choerodon.message.api.vo.MessageSettingWarpVO;
import io.choerodon.message.app.service.MessageSettingC7nService;
import io.choerodon.message.infra.config.C7nSwaggerApiConfig;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api("MessageSetting Organization Controller")
@RestController
@RequestMapping("/choerodon/v1/organizations/{organization_id}/message_settings")
public class MessageSettingOrganizationController {


    @Autowired
    private MessageSettingC7nService messageSettingC7nService;

    @GetMapping
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiModelProperty(value = "查询组织层的通知配置")
    public ResponseEntity<MessageSettingWarpVO> queryMessageSettings(
            @PathVariable(value = "organization_id") Long organizationId,
            @RequestParam(value = "notify_type") String notifyType) {
        return ResponseEntity.ok(messageSettingC7nService.queryOrgMessageSettings(organizationId, notifyType));
    }

    @PutMapping("/batch")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "批量修改消息设置")
    public ResponseEntity<Void> batchOrgUpdateByType(
            @PathVariable(value = "organization_id") Long organizationId,
            @RequestParam(value = "notify_type") String notifyType,
            @RequestBody List<CustomMessageSettingVO> messageSettingVOS) {
        messageSettingC7nService.batchOrgUpdateByType(organizationId, notifyType, messageSettingVOS);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by_type")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiModelProperty(value = "根据项目id,业务code,返回组织层的发送设置")
    public ResponseEntity<MessageSettingVO> getSettingByCode(
            @PathVariable(value = "organization_id") Long sourceId,
            @RequestParam(value = "notify_type") String notifyType,
            @RequestParam(value = "code") String code) {
        return ResponseEntity.ok(messageSettingC7nService.getSettingByCode(sourceId, notifyType, code));
    }
}
