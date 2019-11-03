package io.choerodon.notify.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.notify.api.dto.TemplateVO;
import io.choerodon.notify.api.service.TemplateService;
import io.choerodon.notify.infra.dto.Template;
import io.choerodon.notify.infra.enums.SendingTypeEnum;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping(value = "/v1/templates")
public class TemplateSiteController {
    private TemplateService templateService;

    public TemplateSiteController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @GetMapping
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "分页查询模版（全局层）")
    @CustomPageRequest
    public ResponseEntity<PageInfo<TemplateVO>> pagingByMessage(@ApiIgnore
                                                                @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                                                @RequestParam String businessType,
                                                                @RequestParam String messageType,
                                                                @RequestParam(required = false) String name,
                                                                @RequestParam(required = false) Boolean predefined,
                                                                @RequestParam(required = false) String params) {
        // 1.校验消息类型
        if (!SendingTypeEnum.isInclude(messageType)) {
            throw new CommonException("error.template.message.type.invalid");
        }
        return new ResponseEntity<>(templateService.pagingTemplateByMessageType(pageable, businessType, messageType, name, predefined, params), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "全局层查询模版详情")
    public ResponseEntity<TemplateVO> getById(@PathVariable("id") long id) {
        return new ResponseEntity<>(templateService.getById(id), HttpStatus.OK);
    }

    @PostMapping
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "全局层创建和更新模版")
    public ResponseEntity<Template> createAndUpdateTemplate(@RequestBody @Validated Template template) {
        if (template.getId() == null) {
            return new ResponseEntity<>(templateService.createTemplate(template), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(templateService.updateTemplate(template), HttpStatus.OK);
        }
    }
}
