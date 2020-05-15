package io.choerodon.message.api.controller.v1;

import io.swagger.annotations.ApiOperation;
import org.hzero.message.app.service.MessageService;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.message.api.vo.WebHookVO;
import io.choerodon.message.api.vo.WebhookRecordVO;
import io.choerodon.message.app.service.WebHookC7nService;
import io.choerodon.message.app.service.WebhookRecordC7nService;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

/**
 * @author scp
 * @date 2020/5/10
 * @description
 */
@RestController
@RequestMapping("choerodon/v1/webhook/{organization_id}")
public class WebHookOrganizationC7nController {

    private WebHookC7nService webHookC7nService;
    private MessageService messageService;
    private WebhookRecordC7nService webhookRecordC7nService;


    public WebHookOrganizationC7nController(WebHookC7nService webHookC7nService, WebhookRecordC7nService webhookRecordC7nService,
                                            MessageService messageService) {
        this.webHookC7nService = webHookC7nService;
        this.messageService = messageService;
    }

    @GetMapping("/web_hooks")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "查询WebHook信息（分页接口）")
    @CustomPageRequest
    public ResponseEntity<Page<WebHookVO>> pagingByMessage(@ApiIgnore
                                                           @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageable,
                                                           @PathVariable(name = "organization_id") Long sourceId,
                                                           @RequestParam(required = false) String messageName,
                                                           @RequestParam(required = false) String type,
                                                           @RequestParam(required = false) Boolean enableFlag,
                                                           @RequestParam(required = false) String params) {
        return new ResponseEntity<>(webHookC7nService.pagingWebHook(pageable, sourceId, ResourceLevel.ORGANIZATION.value(), messageName, type, enableFlag, params), HttpStatus.OK);
    }

    @GetMapping("/web_hooks/check_path")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "校验WebHook地址是否已经存在")
    public ResponseEntity<Boolean> check(
            @PathVariable(name = "organization_id") Long organizationId,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam("path") String path) {
        return new ResponseEntity<>(webHookC7nService.checkPath(id, path), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "组织层新增WebHook")
    @PostMapping("/web_hooks")
    public ResponseEntity<WebHookVO> createInOrg(@PathVariable(name = "organization_id") Long organizationId,
                                                 @RequestBody @Validated WebHookVO webHookVO) {
        return new ResponseEntity<>(webHookC7nService.create(organizationId, webHookVO, ResourceLevel.ORGANIZATION.value()), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "更新WebHook")
    @PutMapping("/web_hooks/{id}")
    public ResponseEntity<WebHookVO> update(@PathVariable("organization_id") Long organizationId,
                                            @PathVariable("id") Long id,
                                            @RequestBody @Validated WebHookVO webHookVO) {
        webHookVO.setServerId(id);
        return new ResponseEntity<>(webHookC7nService.update(organizationId, webHookVO, ResourceLevel.ORGANIZATION.value()), HttpStatus.OK);
    }


    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "删除WebHook")
    @DeleteMapping("/web_hooks/{id}")
    public ResponseEntity delete(
            @PathVariable("organization_id") Long organizationId,
            @PathVariable("id") Long id) {
        webHookC7nService.delete(organizationId, id, ResourceLevel.ORGANIZATION.value());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "禁用WebHook")
    @PutMapping("/web_hooks/{id}/disabled")
    public ResponseEntity updateEnabledFlag(
            @PathVariable("organization_id") Long organizationId,
            @PathVariable("id") Long id,
            @RequestParam(value = "enable_flag") Boolean enableFlag) {
        webHookC7nService.updateEnabledFlag(id, enableFlag);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "组织层重试发送记录")
    @GetMapping("/{record_id}/retry")
    public ResponseEntity retry(
            @PathVariable("organization_id") Long organizationId,
            @PathVariable("record_id") Long recordId) {
        messageService.resendMessage(organizationId, recordId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据ID查询webhook")
    @GetMapping("/{webhook_id}")
    public ResponseEntity<WebHookVO> queryById(
            @PathVariable("organization_id") Long organizationId,
            @PathVariable("webhook_id") Long webHookId) {
        return new ResponseEntity<>(webHookC7nService.queryById(webHookId), HttpStatus.OK);
    }


    @GetMapping
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "查询WebHook发送记录(分页接口)")
    @CustomPageRequest
    public ResponseEntity<Page<WebhookRecordVO>> pagingByMessage(@ApiIgnore
                                                                 @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                                 @PathVariable(name = "organization_id") Long sourceId,
                                                                 @RequestParam(name = "webhook_id", required = false) Long webhookId,
                                                                 @RequestParam(required = false) String status,
                                                                 @RequestParam(required = false, name = "name") String eventName,
                                                                 @RequestParam(required = false, name = "type") String type) {

        return new ResponseEntity<>(webhookRecordC7nService.pagingWebHookRecord(pageRequest, sourceId, webhookId, status, eventName, type, ResourceLevel.ORGANIZATION.value()), HttpStatus.OK);
    }

    @ApiOperation(value = "查询WebHook发送记录详情")
    @GetMapping("/details/{record_id}")
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity<WebhookRecordVO> getWebhookRecordDetails(
            @PathVariable(name = "organization_id") Long organizationId,
            @PathVariable(name = "record_id") Long recordId) {
        return new ResponseEntity<>(webhookRecordC7nService.queryById(organizationId, recordId, ResourceLevel.ORGANIZATION.value()), HttpStatus.OK);
    }

}
