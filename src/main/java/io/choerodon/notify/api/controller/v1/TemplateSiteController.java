package io.choerodon.notify.api.controller.v1;

import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.notify.api.service.TemplateService;
import io.choerodon.notify.infra.dto.Template;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/templates")
public class TemplateSiteController {
    private TemplateService templateService;

    public TemplateSiteController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @PostMapping
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "创建和更新模版")
    public ResponseEntity<Template> createAndUpdateTemplate(@RequestBody @Validated Template template) {
        if (template.getId() == null) {
            return new ResponseEntity<>(templateService.createTemplate(template), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(templateService.updateTemplate(template), HttpStatus.OK);
        }
    }
}
