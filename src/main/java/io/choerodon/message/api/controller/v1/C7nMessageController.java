package io.choerodon.message.api.controller.v1;

import io.choerodon.message.api.vo.ProjectMessageVO;
import io.choerodon.message.app.service.MessageSettingC7nService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.message.app.service.C7nMessageService;
import io.choerodon.message.infra.config.C7nSwaggerApiConfig;
import io.choerodon.swagger.annotation.Permission;

import java.util.List;

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
    private MessageSettingC7nService messageSettingC7nService;

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
}
