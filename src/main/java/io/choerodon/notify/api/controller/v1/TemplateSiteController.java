package io.choerodon.notify.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.base.domain.Sort;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.SortDefault;
import io.choerodon.notify.api.dto.TemplateCreateVO;
import io.choerodon.notify.api.dto.TemplateVO;
import io.choerodon.notify.api.pojo.MessageType;
import io.choerodon.notify.api.service.TemplateService;
import io.choerodon.notify.api.validator.Insert;
import io.choerodon.notify.api.validator.Update;
import io.choerodon.notify.domain.Template;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping(value = "/v1/templates")
public class TemplateSiteController {
    TemplateService templateService;

    public TemplateSiteController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @GetMapping
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "分页查询模版（全局层）")
    @CustomPageRequest
    public ResponseEntity<PageInfo<TemplateVO>> pagingByMessage(@ApiIgnore
                                                                @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                                @RequestParam String businessType,
                                                                @RequestParam String messageType,
                                                                @RequestParam(required = false) String name,
                                                                @RequestParam(required = false) Boolean predefined,
                                                                @RequestParam(required = false) String[] params) {
        // 1.校验消息类型
        if (!MessageType.isInclude(messageType)) {
            throw new CommonException("error.template.message.type.invalid");
        }
        // 2.设置过滤信息
        Template filterDTO = new Template();
        filterDTO.setBusinessType(businessType);
        filterDTO.setMessageType(messageType.toLowerCase());
        if (name != null) {
            filterDTO.setName(name);
        }
        if (predefined != null) {
            filterDTO.setIsPredefined(predefined);
        }
        return new ResponseEntity<>(templateService.pagingTemplateByMessageType(filterDTO, params, pageRequest), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    @Permission(type = ResourceType.SITE)
    @ApiModelProperty(value = "全局层查询模版详情")
    public ResponseEntity<TemplateVO> getById(@PathVariable("id") long id) {
        return new ResponseEntity<>(templateService.getById(id), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    @Permission(type = ResourceType.SITE)
    @ApiModelProperty(value = "全局层删除模版")
    public ResponseEntity<Boolean> deleteById(@PathVariable("id") long id) {
        return new ResponseEntity(templateService.deleteById(id), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}")
    @Permission(type = ResourceType.SITE)
    @ApiModelProperty(value = "全局层设为当前模版")
    public ResponseEntity<Boolean> setToTheCurrent(@PathVariable("id") long id) {
        return new ResponseEntity(templateService.setToTheCurrent(id), HttpStatus.OK);
    }


    @PostMapping(value = "/email")
    @Permission(type = ResourceType.SITE)
    @ApiModelProperty(value = "全局层创建邮件模版")
    public ResponseEntity<TemplateCreateVO> createEmailTemplate(@RequestParam(value = "set_to_the_current") Boolean current,
                                                                @RequestBody @Validated({Insert.class}) TemplateCreateVO.EmailTemplateCreateVO createVO) {
        return new ResponseEntity<>(templateService.createTemplate(current, createVO), HttpStatus.OK);
    }

    @PostMapping(value = "/pm")
    @Permission(type = ResourceType.SITE)
    @ApiModelProperty(value = "全局层创建站内信模版")
    public ResponseEntity<TemplateCreateVO> createPmTemplate(@RequestParam(value = "set_to_the_current") Boolean current,
                                                             @RequestBody @Validated({Insert.class}) TemplateCreateVO.PmTemplateCreateVO createVO) {
        return new ResponseEntity<>(templateService.createTemplate(current, createVO), HttpStatus.OK);
    }

    @PostMapping(value = "/sms")
    @Permission(type = ResourceType.SITE)
    @ApiModelProperty(value = "全局层创建短信模版")
    public ResponseEntity<TemplateCreateVO> createSmsTemplate(@RequestParam(value = "set_to_the_current") Boolean current,
                                                              @RequestBody @Validated({Insert.class}) TemplateCreateVO.SmsTemplateCreateVO createVO) {
        return new ResponseEntity<>(templateService.createTemplate(current, createVO), HttpStatus.OK);
    }

    @PutMapping(value = "/email/{id}")
    @Permission(type = ResourceType.SITE)
    @ApiModelProperty(value = "全局层更新邮件模版")
    public ResponseEntity<TemplateCreateVO> updateEmailTemplate(@PathVariable("id") long id,
                                                                @RequestParam(value = "set_to_the_current") Boolean current,
                                                                @RequestBody @Validated({Update.class}) TemplateCreateVO.EmailTemplateCreateVO updateVO) {
        return new ResponseEntity<>(templateService.updateTemplate(current, updateVO), HttpStatus.OK);
    }

    @PutMapping(value = "/pm/{id}")
    @Permission(type = ResourceType.SITE)
    @ApiModelProperty(value = "全局层更新站内信模版")
    public ResponseEntity<TemplateCreateVO> updatePmTemplate(@PathVariable("id") long id,
                                                             @RequestParam(value = "set_to_the_current") Boolean current,
                                                             @RequestBody @Validated({Update.class}) TemplateCreateVO.PmTemplateCreateVO updateVO) {
        return new ResponseEntity<>(templateService.updateTemplate(current, updateVO), HttpStatus.OK);
    }

    @PutMapping(value = "/sms/{id}")
    @Permission(type = ResourceType.SITE)
    @ApiModelProperty(value = "全局层更新短信模版")
    public ResponseEntity<TemplateCreateVO> updateSmsTemplate(@PathVariable("id") long id,
                                                              @RequestParam(value = "set_to_the_current") Boolean current,
                                                              @RequestBody @Validated({Update.class}) TemplateCreateVO.SmsTemplateCreateVO updateVO) {
        return new ResponseEntity<>(templateService.updateTemplate(current, updateVO), HttpStatus.OK);
    }

}
