package io.choerodon.message.api.controller.v1;

import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.message.app.service.EmailTemplateConfigService;
import io.choerodon.message.infra.config.C7nSwaggerApiConfig;
import io.choerodon.message.infra.dto.EmailTemplateConfigDTO;
import io.choerodon.message.infra.dto.iam.TenantDTO;
import io.choerodon.swagger.annotation.Permission;

/**
 * 〈功能简述〉
 * 〈邮件模板配置Controller〉
 *
 * @author 史常萍
 * @Date 2022/09/26 17:55
 */
@Api(tags = C7nSwaggerApiConfig.CHOERODON_MAIL_RECORD)
@RestController
@RequestMapping("/choerodon/v1/email/config")
public class EmailTemplateConfigController {

    @Autowired
    private EmailTemplateConfigService emailTemplateConfigService;

    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/site/create_or_update")
    @ApiOperation(value = "平台层-创建或者更新邮件模板配置")
    public ResponseEntity<Void> siteCreateOrUpdate(@RequestBody @Valid EmailTemplateConfigDTO emailTemplateConfigDTO) {
        emailTemplateConfigDTO.setTenantId(TenantDTO.DEFAULT_TENANT_ID);
        emailTemplateConfigService.createOrUpdateConfig(emailTemplateConfigDTO);
        return Results.success();
    }

    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/site/query_config")
    @ApiOperation(value = "平台层-查询邮件模板配置")
    public ResponseEntity<EmailTemplateConfigDTO> siteQueryConfigByTenantId() {
        return Results.success(emailTemplateConfigService.queryConfigByTenantId(TenantDTO.DEFAULT_TENANT_ID));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/{tenant_id}/create_or_update")
    @ApiOperation(value = "创建或者更新邮件模板配置")
    public ResponseEntity<Void> createOrUpdate(@PathVariable("tenant_id") Long tenantId,
                                               @RequestBody @Valid EmailTemplateConfigDTO emailTemplateConfigDTO) {
        emailTemplateConfigDTO.setTenantId(tenantId);
        emailTemplateConfigService.createOrUpdateConfig(emailTemplateConfigDTO);
        return Results.success();
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{tenant_id}")
    @ApiOperation(value = "查询邮件模板配置")
    public ResponseEntity<EmailTemplateConfigDTO> queryConfigByTenantId(@PathVariable("tenant_id") Long tenantId,
                                                                        @RequestParam(value = "default_config", defaultValue = "false") Boolean defaultConfig) {
        EmailTemplateConfigDTO emailTemplateConfigDTO;
        if (defaultConfig) {
            emailTemplateConfigDTO = emailTemplateConfigService.queryConfigByTenantId(TenantDTO.DEFAULT_TENANT_ID);
        } else {
            emailTemplateConfigDTO = emailTemplateConfigService.queryConfigByTenantId(tenantId);
        }
        return Results.success(emailTemplateConfigDTO);
    }

    @Permission(permissionLogin = true)
    @GetMapping("/preview")
    @ApiOperation(value = "预览邮件模板")
    public ResponseEntity<String> previewTemplate(@RequestBody EmailTemplateConfigDTO emailTemplateConfigDTO) {
        return Results.success(emailTemplateConfigService.previewTemplate(emailTemplateConfigDTO));
    }
}
