package io.choerodon.notify.api.controller.v1;

import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.notify.api.service.SmsService;
import io.choerodon.notify.domain.SmsConfigDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author superlee
 * @since 2019-05-17
 */
@RestController
@RequestMapping("/v1/sms")
public class SmsController {

    private SmsService smsService;

    public SmsController(SmsService smsService) {
        this.smsService = smsService;
    }

    @GetMapping("/config")
    @ApiOperation(value = "根据id查询短信配置")
    @Permission(type = ResourceType.SITE)
    public ResponseEntity<SmsConfigDTO> queryConfig(@RequestParam(name = "organization_id") Long organizationId) {
        return new ResponseEntity<>(smsService.queryConfig(organizationId), HttpStatus.OK);
    }

    @PutMapping("/config/{id}")
    @ApiOperation(value = "更新短信配置")
    @Permission(type = ResourceType.SITE)
    public ResponseEntity<SmsConfigDTO> updateConfig(@PathVariable("id") Long id,
                                                     @RequestBody @Validated SmsConfigDTO smsConfigDTO) {
        return new ResponseEntity<>(smsService.updateConfig(id, smsConfigDTO), HttpStatus.OK);
    }
}
