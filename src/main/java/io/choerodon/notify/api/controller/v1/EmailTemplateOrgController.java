package io.choerodon.notify.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.annotation.Permission;;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.notify.api.dto.EmailTemplateDTO;
import io.choerodon.notify.api.dto.TemplateNamesDTO;
import io.choerodon.notify.api.dto.TemplateQueryDTO;
import io.choerodon.notify.api.service.EmailTemplateService;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "组织层分页查询邮件模版")
    @CustomPageRequest
    public ResponseEntity<PageInfo<TemplateQueryDTO>> pageOrganization(@PathVariable("organization_id") long id,
                                                                       @ApiIgnore
                                                                       @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                                                       @RequestParam(required = false) String name,
                                                                       @RequestParam(required = false) String code,
                                                                       @RequestParam(required = false) String type,
                                                                       @RequestParam(required = false) Boolean isPredefined,
                                                                       @RequestParam(required = false) String params) {
        TemplateQueryDTO query = new TemplateQueryDTO(name, code, type, isPredefined, params);
        return new ResponseEntity<>(templateService.pageByLevel(query, LEVEL_ORG, pageable.getPageNumber(), pageable.getPageSize()), HttpStatus.OK);
    }

    @GetMapping("/names/organizations/{organization_id}")
    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "组织层查询所有邮件模版名")
    public ResponseEntity<List<TemplateNamesDTO>> listNames(@PathVariable("organization_id") long id,
                                                            @RequestParam(required = false, name = "business_type") String businessType) {
        return new ResponseEntity<>(templateService.listNames(LEVEL_ORG, businessType), HttpStatus.OK);
    }

    @GetMapping("/{id}/organizations/{organization_id}")
    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "组织层查询邮件模版详情")
    public ResponseEntity<EmailTemplateDTO> query(@PathVariable("organization_id") long orgId, @PathVariable Long id) {
        return new ResponseEntity<>(templateService.query(id), HttpStatus.OK);
    }

    @PostMapping("/organizations/{organization_id}")
    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "组织层创建邮件模版")
    public ResponseEntity<EmailTemplateDTO> create(@PathVariable("organization_id") long id, @RequestBody @Valid EmailTemplateDTO template) {
        template.setIsPredefined(false);
        template.setId(null);
        template.setObjectVersionNumber(null);
        return new ResponseEntity<>(templateService.create(template), HttpStatus.OK);
    }

    @PutMapping("/{id}/organizations/{organization_id}")
    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "组织层更新邮件模版")
    public ResponseEntity<EmailTemplateDTO> update(@PathVariable("organization_id") long orgId,
                                                   @PathVariable Long id,
                                                   @RequestBody @Valid EmailTemplateDTO template) {
        template.setId(id);
        template.setIsPredefined(null);
        if (template.getObjectVersionNumber() == null) {
            throw new CommonException("error.emailTemplate.objectVersionNumberNull");
        }
        return new ResponseEntity<>(templateService.update(template), HttpStatus.OK);
    }

    @DeleteMapping("/{id}/organizations/{organization_id}")
    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "组织层删除邮件模版")
    public void delete(@PathVariable("organization_id") long orgId,
                       @PathVariable Long id) {
        templateService.delete(id);
    }

    @GetMapping("/check/organizations/{organization_id}")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "组织层检查编码")
    public void check(@PathVariable("organization_id") long orgId,
                      @RequestParam("code") String code) {
        templateService.check(code);
    }

}
