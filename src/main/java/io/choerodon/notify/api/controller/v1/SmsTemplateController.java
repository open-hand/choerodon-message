package io.choerodon.notify.api.controller.v1;

import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.notify.api.dto.TemplateNamesDTO;
import io.choerodon.notify.api.service.SmsTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author superlee
 * @since 2019-05-21
 */
@RestController
@RequestMapping("/v1/notices/sms/templates")
@Api("短信信模版接口")
public class SmsTemplateController {

    private SmsTemplateService smsTemplateService;

    public SmsTemplateController(SmsTemplateService smsTemplateService) {
        this.smsTemplateService = smsTemplateService;
    }

    @GetMapping("/names")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "全局层查询所有短信模版名")
    public ResponseEntity<List<TemplateNamesDTO>> listNames(@RequestParam(required = false, name = "business_type") String businessType) {
        return new ResponseEntity<>(smsTemplateService.listNames(null, businessType), HttpStatus.OK);
    }
}
