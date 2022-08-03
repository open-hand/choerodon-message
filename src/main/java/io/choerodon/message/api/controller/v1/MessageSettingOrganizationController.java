package io.choerodon.message.api.controller.v1;


import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.message.api.vo.MessageSettingWarpVO;
import io.choerodon.message.app.service.MessageSettingC7nService;
import io.choerodon.message.infra.config.C7nSwaggerApiConfig;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @PathVariable(value = "notify_type") String notifyType) {
        return ResponseEntity.ok(messageSettingC7nService.queryOrgMessageSettings(organizationId, notifyType));
    }
}
