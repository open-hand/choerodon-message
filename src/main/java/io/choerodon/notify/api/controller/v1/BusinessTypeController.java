package io.choerodon.notify.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.notify.api.service.BusinessTypeService;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("v1/notices/business_types")
public class BusinessTypeController {

    private BusinessTypeService businessTypeService;

    public BusinessTypeController(BusinessTypeService businessTypeService) {
        this.businessTypeService = businessTypeService;
    }

    @GetMapping
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "获取业务类型名称列表列表")
    public ResponseEntity<Set<String>> listNames() {
        return new ResponseEntity<>(businessTypeService.listNames(), HttpStatus.OK);
    }

}
