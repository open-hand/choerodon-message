package io.choerodon.notify.api.controller.v1;

import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.notify.api.dto.MessageSettingVO;
import io.choerodon.notify.api.service.MessageSettingService;
import io.choerodon.notify.api.vo.CustomMessageSettingVO;
import io.choerodon.notify.api.vo.MessageSettingWarpVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User: Mr.Wang
 * Date: 2019/12/3
 */
@RestController
@RequestMapping("/v1/projects/{project_id}/message_settings")
@Api("发送消息设置接口")
public class MessageSettingController {
    @Autowired
    private MessageSettingService messageSettingService;

    @GetMapping("/type/{notify_type}/code/{code}")
    @Permission(type = ResourceType.PROJECT, roles = InitRoleCode.PROJECT_OWNER)
    @ApiModelProperty(value = "根据项目id,业务code,返回项目层的发送设置")
    public ResponseEntity<MessageSettingVO> getSettingByCode(
            @PathVariable(value = "project_id") Long projectId,
            @PathVariable(value = "notify_type") String notifyType,
            @PathVariable(value = "code") String code,
            @RequestParam(value = "env_id", required = false) Long envId,
            @RequestParam(value = "event_name", required = false) String eventName
            ) {
        return ResponseEntity.ok(messageSettingService.getSettingByCode(projectId, notifyType, code, envId, eventName));
    }
    @GetMapping("/{notify_type}")
    @Permission(type = ResourceType.PROJECT, roles = InitRoleCode.PROJECT_OWNER)
    @ApiModelProperty(value = "根据通知类型，查询通知设置列表")
    public ResponseEntity<MessageSettingWarpVO> listByType(
            @PathVariable(value = "project_id") Long projectId,
            @PathVariable(value = "notify_type") String notifyType,
            @ApiParam(value = "事件名称（过滤条件）")
            @RequestParam(value = "params", required = false) String eventName) {
        return ResponseEntity.ok(messageSettingService.listMessageSettingByType(projectId, notifyType, eventName));
    }
    @PutMapping("/{notify_type}/batch")
    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "批量修改消息设置")
    public ResponseEntity<Void> batchUpdateByType(
            @PathVariable(value = "project_id") Long projectId,
            @PathVariable(value = "notify_type") String notifyType,
            @RequestBody List<CustomMessageSettingVO> messageSettingVOS) {
        messageSettingService.batchUpdateByType(projectId, notifyType, messageSettingVOS);
        return ResponseEntity.noContent().build();
    }
}
