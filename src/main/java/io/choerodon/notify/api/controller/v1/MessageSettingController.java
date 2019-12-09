package io.choerodon.notify.api.controller.v1;

import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.notify.NoticeSendDTO;
import io.choerodon.notify.api.dto.MessageSettingVO;
import io.choerodon.notify.api.dto.SendSettingDetailDTO;
import io.choerodon.notify.api.dto.TargetUserVO;
import io.choerodon.notify.api.service.MessageSettingService;
import io.choerodon.notify.infra.dto.MessageSettingDTO;
import io.choerodon.notify.infra.dto.TargetUserDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * User: Mr.Wang
 * Date: 2019/12/3
 */
@RestController
@RequestMapping("v1/notices/{project_id}/message/setting")
@Api("发送消息设置接口")
public class MessageSettingController {
    @Autowired
    private MessageSettingService messageSettingService;

    @PostMapping("/list")
    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "根据消息类型模糊查询消息发送设置,消息类型为空查询所有,消息类型不为空")
    public ResponseEntity<List<MessageSettingVO>> listMessageSetting(
            @PathVariable(value = "project_id") Long projectId,
            @Valid @RequestBody MessageSettingVO messageSettingVO) {
        return Optional.ofNullable(messageSettingService.listMessageSetting(projectId, messageSettingVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.messageSetting.query"));
    }

    @PutMapping
    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "修改消息设置,支持批量修改")
    public void updateMessageSetting(
            @PathVariable(value = "project_id") Long projectId,
            @RequestBody List<MessageSettingVO> messageSettingVOS) {
        messageSettingService.updateMessageSetting(projectId, messageSettingVOS);
    }

    @GetMapping("/target/user/list")
    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "项目层敏捷消息和devops消息发送前校验接收对象")
    public ResponseEntity<List<TargetUserVO>> getProjectLevelTargetUser(
            @PathVariable(value = "project_id") Long projectId,
            @RequestParam String code) {
        return Optional.ofNullable(messageSettingService.getProjectLevelTargetUser(projectId, code))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.messageSetting.check.TargetUser"));
    }
    @GetMapping("/{code}")
    @Permission(type = ResourceType.PROJECT, roles = InitRoleCode.PROJECT_OWNER)
    @ApiModelProperty(value = "根据项目id,业务code,返回项目层的发送设置")
    public ResponseEntity<MessageSettingVO> getMessageSetting(
            @PathVariable(value = "project_id") Long projectId,
            @RequestParam String code) {
        return Optional.ofNullable(messageSettingService.getMessageSetting(projectId, code))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.messageSetting.query"));
    }
}
