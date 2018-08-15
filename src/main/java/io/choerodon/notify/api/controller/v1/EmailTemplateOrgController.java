package io.choerodon.notify.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.notify.api.dto.EmailTemplateDTO;
import io.choerodon.notify.api.dto.EmailTemplateQueryDTO;
import io.choerodon.notify.api.dto.TemplateNamesDTO;
import io.choerodon.notify.api.service.EmailTemplateService;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

import static io.choerodon.notify.infra.config.NotifyProperties.LEVEL_ORG;

@RestController
@RequestMapping("v1/notices/emails/templates")
@Api("组织层邮件模版接口")
public class EmailTemplateOrgController {

    private EmailTemplateService templateService;

    public EmailTemplateOrgController(EmailTemplateService templateService) {
        this.templateService = templateService;
    }

    @GetMapping("/organizations/{organization_id}")
    @CustomPageRequest
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "组织层分页查询邮件模版")
    public ResponseEntity<Page<EmailTemplateQueryDTO>> pageOrganization(@PathVariable("organization_id") long id,
                                                                        @ApiIgnore @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                                        @RequestParam(required = false) String name,
                                                                        @RequestParam(required = false) String code,
                                                                        @RequestParam(required = false) String type,
                                                                        @RequestParam(required = false) Boolean isPredefined,
                                                                        @RequestParam(required = false) String params) {
        EmailTemplateQueryDTO query = new EmailTemplateQueryDTO(name, code, type, isPredefined, params, pageRequest);
        return new ResponseEntity<>(templateService.pageByLevel(query, LEVEL_ORG), HttpStatus.OK);
    }

    @GetMapping("/names/organizations/{organization_id}")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "组织层查询所有邮件模版名")
    public ResponseEntity<List<TemplateNamesDTO>> listNames(@PathVariable("organization_id") long id) {
        return new ResponseEntity<>(templateService.listNames(LEVEL_ORG), HttpStatus.OK);
    }

    @GetMapping("/{id}/organizations/{organization_id}")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "组织层查询邮件模版详情")
    public ResponseEntity<EmailTemplateDTO> query(@PathVariable("organization_id") long orgId, @PathVariable Long id) {
        return new ResponseEntity<>(templateService.query(id), HttpStatus.OK);
    }

    @PostMapping("/organizations/{organization_id}")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "组织层创建邮件模版")
    public ResponseEntity<EmailTemplateDTO> create(@PathVariable("organization_id") long id, @RequestBody @Valid EmailTemplateDTO template) {
        template.setIsPredefined(false);
        template.setId(null);
        template.setObjectVersionNumber(null);
        return new ResponseEntity<>(templateService.create(template), HttpStatus.OK);
    }

    @PutMapping("/{id}/organizations/{organization_id}")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "组织层更新邮件模版")
    public ResponseEntity<EmailTemplateDTO> update(@PathVariable("organization_id") long orgId,
                                                   @PathVariable Long id,
                                                   @RequestBody EmailTemplateDTO template) {
        template.setId(id);
        template.setIsPredefined(null);
        if (template.getObjectVersionNumber() == null) {
            throw new CommonException("error.emailTemplate.objectVersionNumberNull");
        }
        return new ResponseEntity<>(templateService.update(template), HttpStatus.OK);
    }
}
