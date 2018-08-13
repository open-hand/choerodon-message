package io.choerodon.notify.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.notify.api.dto.SendSettingListDTO;
import io.choerodon.notify.api.dto.SendSettingUpdateDTO;
import io.choerodon.notify.api.service.SendSettingService;
import io.choerodon.notify.domain.SendSetting;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Set;

@RestController
@RequestMapping("v1/notices/send_settings")
public class SendSettingController {

    private SendSettingService sendSettingService;

    public SendSettingController(SendSettingService sendSettingService) {
        this.sendSettingService = sendSettingService;
    }

    @GetMapping("/names")
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "获取业务类型名称列表列表")
    public ResponseEntity<Set<String>> listNames() {
        return new ResponseEntity<>(sendSettingService.listNames(), HttpStatus.OK);
    }

    @GetMapping
    @CustomPageRequest
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "分页查询发送设置列表")
    public ResponseEntity<Page<SendSettingListDTO>> page(@ApiIgnore @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                         @RequestParam(required = false) String name,
                                                         @RequestParam(required = false) String code,
                                                         @RequestParam(required = false) String description,
                                                         @RequestParam(required = false) String params) {
        return new ResponseEntity<>(sendSettingService.page(name, code, description, params, pageRequest), HttpStatus.OK);
    }




    @PutMapping("/{id}")
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "更新发送设置")
    public ResponseEntity<SendSetting> update(@PathVariable Long id,
                                              @RequestBody SendSettingUpdateDTO updateDTO) {
        updateDTO.setId(id);
        return new ResponseEntity<>(sendSettingService.update(updateDTO), HttpStatus.OK);
    }


}
