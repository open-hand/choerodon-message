package io.choerodon.notify.api.controller.v1;

import java.util.List;
import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.notify.api.dto.EmailTemplateDTO;
import io.choerodon.notify.api.dto.TemplateNamesDTO;
import io.choerodon.notify.api.dto.TemplateQueryDTO;
import io.choerodon.notify.api.service.EmailTemplateService;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

@RestController
@RequestMapping("v1/notices/emails/templates")
@Api("全局层邮件模版接口")
public class EmailTemplateSiteController {

    private EmailTemplateService templateService;

    public EmailTemplateSiteController(EmailTemplateService templateService) {
        this.templateService = templateService;
    }

    @GetMapping
    @CustomPageRequest
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "全局层分页查询邮件模版")
    public ResponseEntity<Page<TemplateQueryDTO>> pageSite(@ApiIgnore @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                           @RequestParam(required = false) String name,
                                                           @RequestParam(required = false) String code,
                                                           @RequestParam(required = false) String type,
                                                           @RequestParam(required = false) Boolean isPredefined,
                                                           @RequestParam(required = false) String params) {
        TemplateQueryDTO query = new TemplateQueryDTO(name, code, type, isPredefined, params, pageRequest);
        return new ResponseEntity<>(templateService.pageByLevel(query, null), HttpStatus.OK);
    }


    @GetMapping("/names")
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "全局层查询所有邮件模版名")
    public ResponseEntity<List<TemplateNamesDTO>> listNames(@RequestParam(required = false, name = "business_type") String businessType) {
        return new ResponseEntity<>(templateService.listNames(null, businessType), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "全局层查询邮件模版详情")
    public ResponseEntity<EmailTemplateDTO> query(@PathVariable Long id) {
        return new ResponseEntity<>(templateService.query(id), HttpStatus.OK);
    }

    @PostMapping("/check")
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "全局层检查编码")
    public void check(@RequestBody String code) {
        templateService.check(code);
    }


    @PostMapping
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "全局层创建邮件模版")
    public ResponseEntity<EmailTemplateDTO> create(@RequestBody @Valid EmailTemplateDTO template) {
        template.setIsPredefined(false);
        template.setId(null);
        template.setObjectVersionNumber(null);
        return new ResponseEntity<>(templateService.create(template), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "全局层更新邮件模版")
    public ResponseEntity<EmailTemplateDTO> update(@PathVariable Long id,
                                                   @RequestBody @Valid EmailTemplateDTO template) {
        template.setId(id);
        template.setIsPredefined(null);
        if (template.getObjectVersionNumber() == null) {
            throw new CommonException("error.emailTemplate.objectVersionNumberNull");
        }
        return new ResponseEntity<>(templateService.update(template), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "全局层删除邮件模版")
    public void delete(@PathVariable Long id) {
        templateService.delete(id);
    }

}
