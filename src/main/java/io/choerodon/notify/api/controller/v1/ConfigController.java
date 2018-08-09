package io.choerodon.notify.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.notify.api.dto.EmailConfigDTO;
import io.choerodon.notify.api.service.ConfigService;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("v1/notices/configs")
public class ConfigController {

    private ConfigService configService;

    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    @PostMapping("/email")
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "更新邮箱配置")
    public ResponseEntity<EmailConfigDTO> createOrUpdate(@RequestBody @Valid EmailConfigDTO configDTO) {
        return new ResponseEntity<>(configService.save(configDTO), HttpStatus.OK);
    }


}
