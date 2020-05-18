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
@RequestMapping("/choerodon/v1/webhook/project/{project_id}")
public class WebHookProjectC7nController {

    private WebHookC7nService webHookC7nService;
    private WebhookRecordC7nService webhookRecordC7nService;


    public WebHookProjectC7nController(WebHookC7nService webHookC7nService, WebhookRecordC7nService webhookRecordC7nService) {
        this.webHookC7nService = webHookC7nService;
        this.webhookRecordC7nService = webhookRecordC7nService;
    }

    @GetMapping("/web_hooks")
    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "项目层 查询WebHook信息（分页接口）")
    @CustomPageRequest
    public ResponseEntity<Page<WebHookVO>> pagingByMessage(@ApiIgnore
                                                           @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageable,
                                                           @PathVariable(name = "project_id") Long sourceId,
                                                           @RequestParam(required = false) String messageName,
                                                           @RequestParam(required = false) String type,
                                                           @RequestParam(required = false) Boolean enableFlag,
                                                           @RequestParam(required = false) String params) {
        return new ResponseEntity<>(webHookC7nService.pagingWebHook(pageable, sourceId, ResourceLevel.PROJECT.value(), messageName, type, enableFlag, params), HttpStatus.OK);
    }

    @GetMapping("/web_hooks/check_path")
    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "校验WebHook地址是否已经存在")
    public ResponseEntity<Boolean> check(
            @PathVariable(name = "project_id") Long projectId,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam("path") String path) {
        return new ResponseEntity<>(webHookC7nService.checkPath(id, path), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "项目层新增WebHook")
    @PostMapping("/web_hooks")
    public ResponseEntity<WebHookVO> createInOrg(@PathVariable(name = "project_id") Long projectId,
                                                 @RequestBody @Validated WebHookVO webHookVO) {
        return new ResponseEntity<>(webHookC7nService.create(projectId, webHookVO, ResourceLevel.PROJECT.value()), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "更新WebHook")
    @PutMapping("/web_hooks/{id}")
    public ResponseEntity<WebHookVO> update(@PathVariable("project_id") Long projectId,
                                            @PathVariable("id") Long id,
                                            @RequestBody @Validated WebHookVO webHookVO) {
        return new ResponseEntity<>(webHookC7nService.update(projectId, webHookVO, ResourceLevel.PROJECT.value()), HttpStatus.OK);
    }


    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "删除WebHook")
    @DeleteMapping("/web_hooks/{id}")
    public ResponseEntity delete(
            @PathVariable("project_id") Long projectId,
            @PathVariable("id") Long id) {
        webHookC7nService.delete(projectId, id, ResourceLevel.PROJECT.value());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "禁用WebHook")
    @PutMapping("/web_hooks/{id}/disabled")
    public ResponseEntity updateEnabledFlag(
            @PathVariable("project_id") Long projectId,
            @PathVariable("id") Long id,
            @RequestParam(value = "enable_flag") Boolean enableFlag) {
        webHookC7nService.updateEnabledFlag(id, enableFlag);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "项目层重试发送记录")
    @GetMapping("/{record_id}/retry")
    public ResponseEntity retry(
            @PathVariable("project_id") Long projectId,
            @PathVariable("record_id") Long recordId) {
        webHookC7nService.resendMessage(projectId, recordId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "根据ID查询webhook")
    @GetMapping("/{webhook_id}")
    public ResponseEntity<WebHookVO> queryById(
            @PathVariable("project_id") Long projectId,
            @PathVariable("webhook_id") Long webHookId) {
        return new ResponseEntity<>(webHookC7nService.queryById(webHookId), HttpStatus.OK);
    }


    @GetMapping
    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "查询WebHook发送记录(分页接口)")
    @CustomPageRequest
    public ResponseEntity<Page<WebhookRecordVO>> pagingByMessage(@ApiIgnore
                                                                 @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                                 @PathVariable(name = "project_id") Long projectId,
                                                                 @RequestParam(name = "webhook_id", required = false) Long webhookId,
                                                                 @RequestParam(required = false) String status,
                                                                 @RequestParam(required = false, name = "name") String eventName,
                                                                 @RequestParam(required = false, name = "type") String type) {

        return new ResponseEntity<>(webhookRecordC7nService.pagingWebHookRecord(pageRequest, projectId, webhookId, status, eventName, type, ResourceLevel.PROJECT.value()), HttpStatus.OK);
    }

    @ApiOperation(value = "查询WebHook发送记录详情")
    @GetMapping("/details/{record_id}")
    @Permission(level = ResourceLevel.PROJECT)
    public ResponseEntity<WebhookRecordVO> getWebhookRecordDetails(
            @PathVariable(name = "project_id") Long projectId,
            @PathVariable(name = "record_id") Long recordId) {
        return new ResponseEntity<>(webhookRecordC7nService.queryById(projectId, recordId, ResourceLevel.PROJECT.value()), HttpStatus.OK);
    }

}
