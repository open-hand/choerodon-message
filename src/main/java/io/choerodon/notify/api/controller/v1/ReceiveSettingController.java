package io.choerodon.notify.api.controller.v1;

import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.notify.api.dto.ReceiveSettingDTO;
import io.choerodon.notify.api.service.ReceiveSettingService;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author dengyouquan
 **/
@RestController
@RequestMapping("v1/notices/receive_setting")
@Api("个人配置是否接收通知接口")
public class ReceiveSettingController {
    private ReceiveSettingService receiveSettingService;

    public ReceiveSettingController(ReceiveSettingService receiveSettingService) {
        this.receiveSettingService = receiveSettingService;
    }

    @GetMapping
    @Permission(permissionLogin = true)
    @ApiOperation(value = "查询当前用户所有接收通知配置")
    public List<ReceiveSettingDTO> queryByUserId() {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        return receiveSettingService.queryByUserId(userId);
    }

    @PutMapping("/all")
    @Permission(permissionLogin = true)
    @ApiOperation(value = "更新当前用户所有接收通知配置（前端传输当前用户所有禁用的接收通知配置）")
    public void update(@RequestBody List<ReceiveSettingDTO> settingDTOList) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        receiveSettingService.update(userId, settingDTOList);
    }

    @PutMapping
    @Permission(permissionLogin = true)
    @ApiOperation(value = "根据组织或项目或平台或全部更新当前用户接收通知配置")
    public void updateByUserId(@RequestParam("message_type") String messageType,
                               @ApiParam(name = "源id", value = "如果为空，则表示禁/启用所有通知")
                               @RequestParam(value = "source_id", required = false) Long sourceId,
                               @ApiParam(name = "源类型", value = "如果为空，则表示禁/启用所有通知", example = "site/project/organization")
                               @RequestParam(value = "source_type", required = false) String sourceType,
                               @RequestParam("disable") boolean disable) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        if (sourceId == null || sourceType == null) {
            receiveSettingService.updateByUserId(userId, messageType, disable);
            return;
        }
        receiveSettingService.updateByUserIdAndSourceTypeAndSourceId(userId, sourceType, sourceId, messageType, disable);

    }
}
