package io.choerodon.notify.api.controller.v1;

import java.util.List;
import javax.validation.Valid;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.constant.PageConstant;
import io.choerodon.base.enums.ResourceType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.exception.CommonException;
import io.choerodon.notify.api.dto.PmTemplateDTO;
import io.choerodon.notify.api.dto.TemplateNamesDTO;
import io.choerodon.notify.api.dto.TemplateQueryDTO;
import io.choerodon.notify.api.service.PmTemplateService;

@RestController
@RequestMapping("v1/notices/letters/templates")
@Api("全局层站内信模版接口")
public class PmTemplateSiteController {

    private PmTemplateService templateService;

    public PmTemplateSiteController(PmTemplateService templateService) {
        this.templateService = templateService;
    }

    @GetMapping
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "全局层分页查询站内信模版")
    public ResponseEntity<PageInfo<TemplateQueryDTO>> pageSite(@RequestParam(defaultValue = PageConstant.PAGE, required = false) final int page,
                                                               @RequestParam(defaultValue = PageConstant.SIZE, required = false) final int size,
                                                               @RequestParam(required = false) String name,
                                                               @RequestParam(required = false) String code,
                                                               @RequestParam(required = false) String type,
                                                               @RequestParam(required = false) Boolean isPredefined,
                                                               @RequestParam(required = false) String params) {
        TemplateQueryDTO query = new TemplateQueryDTO(name, code, type, isPredefined, params);
        return new ResponseEntity<>(templateService.pageByLevel(query, null, page, size), HttpStatus.OK);
    }


    @GetMapping("/names")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "全局层查询所有站内信模版名")
    public ResponseEntity<List<TemplateNamesDTO>> listNames(@RequestParam(required = false, name = "business_type") String businessType) {
        return new ResponseEntity<>(templateService.listNames(null, businessType), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "全局层查询站内信模版详情")
    public ResponseEntity<PmTemplateDTO> query(@PathVariable Long id) {
        return new ResponseEntity<>(templateService.query(id), HttpStatus.OK);
    }

    @PostMapping("/check")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "全局层检查编码")
    public void check(@RequestBody String code) {
        templateService.check(code);
    }


    @PostMapping
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "全局层创建站内信模版")
    public ResponseEntity<PmTemplateDTO> create(@RequestBody @Valid PmTemplateDTO template) {
        template.setIsPredefined(false);
        template.setId(null);
        template.setObjectVersionNumber(null);
        return new ResponseEntity<>(templateService.create(template), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "全局层更新站内信模版")
    public ResponseEntity<PmTemplateDTO> update(@PathVariable Long id,
                                                @RequestBody @Valid PmTemplateDTO template) {
        template.setId(id);
        template.setIsPredefined(null);
        if (template.getObjectVersionNumber() == null) {
            throw new CommonException("error.pmTemplate.objectVersionNumberNull");
        }
        return new ResponseEntity<>(templateService.update(template), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "全局层删除站内信模版")
    public void delete(@PathVariable Long id) {
        templateService.delete(id);
    }

}
