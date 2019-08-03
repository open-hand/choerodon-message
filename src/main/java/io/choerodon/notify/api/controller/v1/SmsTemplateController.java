package io.choerodon.notify.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.constant.PageConstant;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.notify.api.dto.TemplateNamesDTO;
import io.choerodon.notify.api.query.TemplateQuery;
import io.choerodon.notify.api.service.SmsTemplateService;
import io.choerodon.notify.domain.Template;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author superlee
 * @since 2019-05-21
 */
@RestController
@RequestMapping("/v1/notices/sms/templates")
@Api("短信信模版接口")
public class SmsTemplateController {

    private SmsTemplateService smsTemplateService;

    public SmsTemplateController(SmsTemplateService smsTemplateService) {
        this.smsTemplateService = smsTemplateService;
    }

    @GetMapping("/names")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "全局层查询所有短信模版名")
    public ResponseEntity<List<TemplateNamesDTO>> listNames(@RequestParam(required = false, name = "business_type") String businessType) {
        return new ResponseEntity<>(smsTemplateService.listNames(null, businessType), HttpStatus.OK);
    }

    @GetMapping
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "全局层分页查询短信模版列表")
    public ResponseEntity<PageInfo<Template>> pagedSearch(@RequestParam(defaultValue = PageConstant.PAGE, required = false) final int page,
                                                          @RequestParam(defaultValue = PageConstant.SIZE, required = false) final int size,
                                                          TemplateQuery templateQuery) {
        return new ResponseEntity<>(smsTemplateService.pagedSearch(page, size, templateQuery), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "根据id查询短信模版列表")
    public ResponseEntity<Template> query(@PathVariable("id") Long id) {
        return new ResponseEntity<>(smsTemplateService.query(id), HttpStatus.OK);
    }

    @PostMapping
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "创建短信模版列表")
    public ResponseEntity<Template> create(@RequestBody @Validated Template templateDTO) {
        return new ResponseEntity<>(smsTemplateService.create(templateDTO), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "根据id更新短信模版列表")
    public ResponseEntity<Template> update(@PathVariable("id") Long id, @RequestBody Template templateDTO) {
        return new ResponseEntity<>(smsTemplateService.update(id, templateDTO), HttpStatus.OK);
    }

    @PostMapping("/check")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "code重名校验")
    public void check(@RequestBody String code) {
        smsTemplateService.check(code);
    }

    @DeleteMapping("/{id}")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "根据id删除自定义短信模版列表")
    public void delete(@PathVariable("id") Long id) {
        smsTemplateService.delete(id);
    }
}
