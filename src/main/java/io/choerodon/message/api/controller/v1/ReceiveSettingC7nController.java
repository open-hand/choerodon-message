package io.choerodon.message.api.controller.v1;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.message.api.vo.ReceiveSettingVO;
import io.choerodon.message.app.service.ReceiveSettingC7nService;
import io.choerodon.swagger.annotation.Permission;

/**
 * @author dengyouquan
 **/
@RestController
@RequestMapping("v1/notices/receive_setting")
@Api("个人配置是否接收通知接口")
public class ReceiveSettingC7nController {
    private ReceiveSettingC7nService receiveSettingService;

    public ReceiveSettingC7nController(ReceiveSettingC7nService receiveSettingService) {
        this.receiveSettingService = receiveSettingService;
    }

    @GetMapping
    @Permission(permissionLogin = true, level = ResourceLevel.SITE)
    @ApiOperation(value = "查询当前用户所有接收通知配置")
    public List<ReceiveSettingVO> queryByUserId(
            @ApiParam(name = "消息类型", required = true)
            @RequestParam("source_type") String sourceType) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        return receiveSettingService.queryByUserId(userId, sourceType);
    }

    @PutMapping("/all")
    @Permission(permissionLogin = true, level = ResourceLevel.SITE)
    @ApiOperation(value = "更新当前用户所有接收通知配置（前端传输当前用户所有禁用的接收通知配置）")
    public void update(@RequestBody List<ReceiveSettingVO> settingDTOList,
                       @ApiParam(name = "消息类型", required = true)
                       @RequestParam("source_type") String sourceType) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        receiveSettingService.update(userId, settingDTOList, sourceType);
    }

}
