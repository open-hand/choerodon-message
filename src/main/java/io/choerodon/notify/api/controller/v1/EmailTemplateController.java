package io.choerodon.notify.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.notify.api.dto.EmailTemplateDTO;
import io.choerodon.notify.api.dto.EmailTemplateQueryDTO;
import io.choerodon.notify.api.service.EmailTemplateService;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@RestController
@RequestMapping("v1/notices/emails/templates")
public class EmailTemplateController {

    private EmailTemplateService templateService;

    public EmailTemplateController(EmailTemplateService templateService) {
        this.templateService = templateService;
    }

    @GetMapping
    @CustomPageRequest
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "分页查询邮件模版")
    public ResponseEntity<Page<EmailTemplateQueryDTO>> page(@ApiIgnore @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                            @RequestParam(required = false) String name,
                                                            @RequestParam(required = false) String code,
                                                            @RequestParam(required = false) String type,
                                                            @RequestParam(required = false) Boolean isPredefined,
                                                            @RequestParam(required = false) String params) {
        EmailTemplateQueryDTO query = new EmailTemplateQueryDTO(name, code, type, isPredefined, params, pageRequest);
        return new ResponseEntity<>(templateService.page(query), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "查询邮件模版详情")
    public ResponseEntity<EmailTemplateDTO> query(@PathVariable Long id) {
        return new ResponseEntity<>(templateService.query(id), HttpStatus.OK);
    }

    @PostMapping
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "创建邮件模版")
    public ResponseEntity<EmailTemplateDTO> create(@RequestBody @Valid EmailTemplateDTO template) {
        template.setIsPredefined(false);
        template.setId(null);
        template.setObjectVersionNumber(null);
        return new ResponseEntity<>(templateService.create(template), HttpStatus.OK);
    }

    @PostMapping("/{id}")
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "更新邮件模版")
    public ResponseEntity<EmailTemplateDTO> update(@PathVariable Long id,
                                                   @RequestBody EmailTemplateDTO template) {
        template.setId(id);
        template.setIsPredefined(null);
        if (template.getObjectVersionNumber() == null) {
            throw new CommonException("error.emailTemplate.objectVersionNumberNull");
        }
        return new ResponseEntity<>(templateService.update(template), HttpStatus.OK);
    }


}
