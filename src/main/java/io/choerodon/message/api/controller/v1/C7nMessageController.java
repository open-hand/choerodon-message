package io.choerodon.message.api.controller.v1;

import java.util.Date;
import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

import java.util.Set;

import org.hzero.boot.message.entity.MessageSender;
import org.hzero.core.util.Results;
import org.hzero.message.api.dto.SimpleMessageDTO;
import org.hzero.message.app.service.MessageService;
import org.hzero.message.infra.constant.HmsgConstant;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.message.api.vo.MessageTrxStatusVO;
import io.choerodon.message.api.vo.ProjectMessageVO;
import io.choerodon.message.app.service.*;
import io.choerodon.message.infra.config.C7nSwaggerApiConfig;
import io.choerodon.swagger.annotation.Permission;

/**
 * @author zmf
 * @since 2020/6/3
 */
@Api(tags = C7nSwaggerApiConfig.CHOEROODN_USER_MESSAGES)
@RestController
@RequestMapping("/choerodon/v1/messages")
public class C7nMessageController {
    @Autowired
    private C7nMessageService c7nMessageService;
    @Autowired
    private RelSendMessageC7nService relSendMessageC7nService;
    @Autowired
    private MessageSettingC7nService messageSettingC7nService;
    @Autowired
    private MessageC7nService messageC7nService;
    @Autowired
    private CleanService cleanService;

    @Permission(permissionLogin = true)
    @ApiOperation("彻底删除用户当前的所有站内信")
    @DeleteMapping("/user/delete_all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearAll() {
        c7nMessageService.deleteAllSiteMessages();
    }


    @PostMapping("/setting/{code}/enabled")
    @Permission(permissionWithin = true)
    @ApiModelProperty(value = "根据消息通知设置code，查询所有配置了启用的项目")
    public ResponseEntity<List<ProjectMessageVO>> listEnabledSettingByCode(@PathVariable(value = "code") String code,
                                                                           @RequestParam(value = "notify_type") String notifyType) {
        return ResponseEntity.ok(messageSettingC7nService.listEnabledSettingByCode(code, notifyType));
    }

    @ApiOperation("发送消息")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping({"/send/batch"})
    public ResponseEntity<Void> batchSendMessage(@RequestBody @Encrypt List<MessageSender> senderList) {
        relSendMessageC7nService.batchSendMessage(senderList);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(permissionLogin = true)
    @ApiOperation(value = "根据messageId查询站内信预览内容")
    @GetMapping("/{message_id}")
    public ResponseEntity<SimpleMessageDTO> getSimpleMessageDTO(@PathVariable(value = "message_id") Long messageId) {
        return ResponseEntity.ok(c7nMessageService.getSimpleMessageDTO(messageId));
    }

    @Permission(permissionLogin = true)
    @ApiOperation(value = "失败邮件重新发送")
    @GetMapping("/resend")
    public ResponseEntity<Void> resend(@RequestParam(required = false) Date endDate) {
        messageC7nService.resendFailedEmail(endDate);
        return Results.success();
    }

    //根据邮件集合查询邮件发送的成功还是失败
    @Permission(permissionWithin = true)
    @ApiOperation(value = "失败邮件重新发送")
    @PostMapping("/trx_status")
    public ResponseEntity<List<MessageTrxStatusVO>> queryTrxStatusCode(
            @RequestParam(value = "templateCode") String templateCode,
            @RequestBody Set<String> userEmails) {
        return Results.success(messageC7nService.queryTrxStatusCode(userEmails, templateCode));
    }

    //根据邮件集合查询邮件发送的成功还是失败
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "清理消息日志")
    @PostMapping("/clean")
    public ResponseEntity<Void> cleanMessageRecord(@RequestParam(value = "clean_strategy", defaultValue = HmsgConstant.DataCleanStrategy.THREE_MONTH) String cleanStrategy) {
        cleanService.clearLog(null, cleanStrategy);
        return Results.success();
    }
}
