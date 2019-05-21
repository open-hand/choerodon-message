package io.choerodon.notify.api.controller.v1;

import java.util.List;
import java.util.Set;
import javax.validation.Valid;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.constant.PageConstant;
import io.choerodon.base.enums.ResourceType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.notify.api.dto.BusinessTypeDTO;
import io.choerodon.notify.api.dto.SendSettingDetailDTO;
import io.choerodon.notify.api.dto.SendSettingListDTO;
import io.choerodon.notify.api.dto.SendSettingUpdateDTO;
import io.choerodon.notify.api.service.SendSettingService;
import io.choerodon.notify.domain.SendSetting;

@RestController
@RequestMapping("v1/notices/send_settings")
@Api("全局层发送设置接口")
public class SendSettingSiteController {

    private SendSettingService sendSettingService;

    public SendSettingSiteController(SendSettingService sendSettingService) {
        this.sendSettingService = sendSettingService;
    }

    @GetMapping("/names")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "全局层获取业务类型名称列表")
    public ResponseEntity<Set<BusinessTypeDTO>> listNames() {
        return new ResponseEntity<>(sendSettingService.listNames(), HttpStatus.OK);
    }

    @GetMapping
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "全局层分页查询发送设置列表")
    public ResponseEntity<PageInfo<SendSettingListDTO>> pageSite(@RequestParam(defaultValue = PageConstant.PAGE, required = false) final int page,
                                                                 @RequestParam(defaultValue = PageConstant.SIZE, required = false) final int size,
                                                                 @RequestParam(required = false) String name,
                                                                 @RequestParam(required = false) String code,
                                                                 @RequestParam(required = false) String level,
                                                                 @RequestParam(required = false) String description,
                                                                 @RequestParam(required = false) String params) {
        return new ResponseEntity<>(sendSettingService.page(level, name, code, description, params, page, size), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "全局层查看发送设置详情")
    public ResponseEntity<SendSettingDetailDTO> query(@PathVariable Long id) {
        return new ResponseEntity<>(sendSettingService.query(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "全局层更新发送设置")
    public ResponseEntity<SendSetting> update(@PathVariable Long id,
                                              @RequestBody @Valid SendSettingUpdateDTO updateDTO) {
        updateDTO.setId(id);
        return new ResponseEntity<>(sendSettingService.update(updateDTO), HttpStatus.OK);
    }

    @GetMapping("/list/allow_config")
    @Permission(permissionLogin = true)
    @ApiOperation(value = "根据层级查询未禁用通知配置的发送设置列表")
    public ResponseEntity<List<SendSettingDetailDTO>> listLevelAndAllowConfig(@RequestParam(name = "source_type", required = false) String level) {
        return new ResponseEntity<>(sendSettingService.queryByLevelAndAllowConfig(level, true), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    @ApiOperation(value = "根据Id删除发送设置")
    public ResponseEntity delSendSetting(@PathVariable Long id) {
        sendSettingService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
