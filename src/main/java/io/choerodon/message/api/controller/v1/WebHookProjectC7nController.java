package io.choerodon.message.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.message.api.vo.WebHookVO;
import io.choerodon.message.api.vo.WebhookRecordVO;
import io.choerodon.message.app.service.SendSettingC7nService;
import io.choerodon.message.app.service.WebHookC7nService;
import io.choerodon.message.app.service.WebhookRecordC7nService;
import io.choerodon.message.infra.config.C7nSwaggerApiConfig;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author scp
 * @date 2020/5/10
 * @description
 */
@Api(tags = C7nSwaggerApiConfig.CHOERODON_WEBHOOK_PROJECT)
@RestController
@RequestMapping("/choerodon/v1/project/{project_id}/web_hooks")
public class WebHookProjectC7nController {

    private final WebHookC7nService webHookC7nService;
    private WebhookRecordC7nService webhookRecordC7nService;
    private SendSettingC7nService sendSettingC7nService;


    public WebHookProjectC7nController(WebHookC7nService webHookC7nService,
                                       WebhookRecordC7nService webhookRecordC7nService,
                                       SendSettingC7nService sendSettingC7nService) {
        this.webHookC7nService = webHookC7nService;
        this.webhookRecordC7nService = webhookRecordC7nService;
        this.sendSettingC7nService = sendSettingC7nService;
    }

    @GetMapping
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "项目层 查询WebHook信息（分页接口）")
    @CustomPageRequest
    public ResponseEntity<Page<WebHookVO>> pageWebHookInfo(
            @ApiIgnore
            @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageable,
            @PathVariable(name = "project_id") Long sourceId,
            @RequestParam(required = false) String messageName,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Boolean enableFlag,
            @RequestParam(required = false) String params) {
        return new ResponseEntity<>(webHookC7nService.pagingWebHook(pageable, sourceId, ResourceLevel.PROJECT.value(), messageName, type, enableFlag, params), HttpStatus.OK);
    }

    @GetMapping("/check_path")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "校验WebHook地址是否已经存在")
    public ResponseEntity<Boolean> check(
            @PathVariable(name = "project_id") Long projectId,
            @Encrypt
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam("path") String path) {
        return new ResponseEntity<>(webHookC7nService.checkPath(id, path), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "项目层新增WebHook")
    @PostMapping
    public ResponseEntity<WebHookVO> createInOrg(
            @PathVariable(name = "project_id") Long projectId,
            @RequestBody @Validated WebHookVO webHookVO) {
        return new ResponseEntity<>(webHookC7nService.create(projectId, webHookVO, ResourceLevel.PROJECT.value()), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "更新WebHook")
    @PutMapping("/{id}")
    public ResponseEntity<WebHookVO> update(
            @PathVariable("project_id") Long projectId,
            @Encrypt
            @PathVariable("id") Long id,
            @RequestBody @Validated WebHookVO webHookVO) {
        return new ResponseEntity<>(webHookC7nService.update(projectId, webHookVO, ResourceLevel.PROJECT.value()), HttpStatus.OK);
    }


    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "删除WebHook")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable("project_id") Long projectId,
            @Encrypt
            @PathVariable("id") Long id) {
        webHookC7nService.delete(projectId, id, ResourceLevel.PROJECT.value());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "禁用WebHook")
    @PutMapping("/{id}/update_status")
    public ResponseEntity<Void> updateEnabledFlag(
            @PathVariable("project_id") Long projectId,
            @Encrypt
            @PathVariable("id") Long id,
            @RequestParam(value = "enable_flag") Boolean enableFlag) {
        webHookC7nService.updateEnabledFlagInProject(projectId, id, enableFlag);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "项目层重试发送记录")
    @GetMapping("/{record_id}/retry")
    public ResponseEntity<Void> retry(
            @PathVariable("project_id") Long projectId,
            @Encrypt
            @PathVariable("record_id") Long recordId) {
        webHookC7nService.resendMessage(projectId, recordId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据ID查询webhook")
    @GetMapping("/{webhook_id}/record")
    public ResponseEntity<WebHookVO> queryById(
            @PathVariable("project_id") Long projectId,
            @Encrypt
            @PathVariable("webhook_id") Long webHookId) {
        return new ResponseEntity<>(webHookC7nService.queryById(webHookId), HttpStatus.OK);
    }


    @GetMapping(value = "/records")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "查询WebHook发送记录(分页接口)")
    @CustomPageRequest
    public ResponseEntity<Page<WebhookRecordVO>> pageWebHookSendRecord(
            @ApiIgnore
            @SortDefault(value = "creationDate", direction = Sort.Direction.DESC) PageRequest pageRequest,
            @PathVariable(name = "project_id") Long projectId,
            @Encrypt
            @RequestParam(name = "webhook_id", required = false) Long webhookId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, name = "name") String eventName,
            @RequestParam(required = false, name = "type") String type) {
        return new ResponseEntity<>(webhookRecordC7nService.pagingWebHookRecord(pageRequest, projectId, webhookId, status, eventName, type, ResourceLevel.PROJECT.value()), HttpStatus.OK);
    }

    @ApiOperation(value = "查询WebHook发送记录详情")
    @GetMapping("/details/{record_id}")
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity<WebhookRecordVO> getWebhookRecordDetails(
            @PathVariable(name = "project_id") Long projectId,
            @Encrypt
            @PathVariable(name = "record_id") Long recordId) {
        return new ResponseEntity<>(webhookRecordC7nService.queryById(projectId, recordId, ResourceLevel.PROJECT.value()), HttpStatus.OK);
    }

    @ApiOperation(value = "项目层可用于创建webhook事件查询")
    @GetMapping("/send_settings")
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity<WebHookVO.SendSetting> getTempServerForWebhook(
            @PathVariable("project_id") Long projectId,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "description", required = false) String description,
            @ApiParam(value = "webhook类型 DingTalk/WeChat/Json")
            @RequestParam(name = "type") String type) {
        return new ResponseEntity<>(sendSettingC7nService.getTempServerForWebhook(projectId, ResourceLevel.PROJECT.value(), name, description, type), HttpStatus.OK);
    }

}
