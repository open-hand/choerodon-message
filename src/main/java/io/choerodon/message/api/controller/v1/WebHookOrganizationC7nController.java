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
import org.hzero.core.util.Results;
import org.hzero.message.app.service.MessageService;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@Api(tags = C7nSwaggerApiConfig.CHOERODON_WEBHOOK_ORGANIZATION)
@RestController
@RequestMapping("/choerodon/v1/organization/{organization_id}/web_hooks")
public class WebHookOrganizationC7nController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebHookOrganizationC7nController.class);


    private final WebHookC7nService webHookC7nService;
    private MessageService messageService;
    private WebhookRecordC7nService webhookRecordC7nService;
    private SendSettingC7nService sendSettingC7nService;


    public WebHookOrganizationC7nController(WebHookC7nService webHookC7nService, WebhookRecordC7nService webhookRecordC7nService,
                                            MessageService messageService,
                                            SendSettingC7nService sendSettingC7nService) {
        this.webHookC7nService = webHookC7nService;
        this.messageService = messageService;
        this.webhookRecordC7nService = webhookRecordC7nService;
        this.sendSettingC7nService = sendSettingC7nService;
    }

    @GetMapping
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "查询WebHook信息（分页接口）")
    @CustomPageRequest
    public ResponseEntity<Page<WebHookVO>> pageWebHookInfo(
            @ApiIgnore
            @SortDefault(value = "server_id", direction = Sort.Direction.DESC) PageRequest pageable,
            @PathVariable(name = "organization_id") Long sourceId,
            @RequestParam(required = false) String messageName,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Boolean enableFlag,
            @RequestParam(required = false) String params) {
        return new ResponseEntity<>(webHookC7nService.pagingWebHook(pageable, sourceId, ResourceLevel.ORGANIZATION.value(), messageName, type, enableFlag, params), HttpStatus.OK);
    }

    @GetMapping("/check_path")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "校验WebHook地址是否已经存在")
    public ResponseEntity<Boolean> check(
            @PathVariable(name = "organization_id") Long organizationId,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam("path") String path) {
        return new ResponseEntity<>(webHookC7nService.checkPath(id, path), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "组织层新增WebHook")
    @PostMapping
    public ResponseEntity<WebHookVO> createInOrg(
            @PathVariable(name = "organization_id") Long organizationId,
            @RequestBody WebHookVO webHookVO) {
        return new ResponseEntity<>(webHookC7nService.create(organizationId, webHookVO, ResourceLevel.ORGANIZATION.value()), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "更新WebHook")
    @PutMapping("/{id}")
    public ResponseEntity<WebHookVO> update(
            @PathVariable("organization_id") Long organizationId,
            @Encrypt
            @PathVariable("id") Long id,
            @RequestBody @Validated WebHookVO webHookVO) {
        webHookVO.setServerId(id);
        return new ResponseEntity<>(webHookC7nService.update(organizationId, webHookVO, ResourceLevel.ORGANIZATION.value()), HttpStatus.OK);
    }


    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "删除WebHook")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable("organization_id") Long organizationId,
            @Encrypt
            @PathVariable("id") Long id) {
        webHookC7nService.delete(organizationId, id, ResourceLevel.ORGANIZATION.value());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "禁用WebHook")
    @PutMapping("/{id}/update_status")
    public ResponseEntity<Void> updateEnabledFlag(
            @PathVariable("organization_id") Long organizationId,
            @Encrypt
            @PathVariable("id") Long id,
            @RequestParam(value = "enable_flag") Boolean enableFlag) {
        webHookC7nService.updateEnabledFlag(organizationId, id, enableFlag);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "组织层重试发送记录")
    @GetMapping("/{record_id}/retry")
    public ResponseEntity<Void> retry(
            @PathVariable("organization_id") Long organizationId,
            @Encrypt
            @PathVariable("record_id") Long recordId) {
        messageService.resendMessage(organizationId, recordId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据ID查询webhook")
    @GetMapping("/{webhook_id}/record")
    public ResponseEntity<WebHookVO> queryById(
            @PathVariable("organization_id") Long organizationId,
            @Encrypt
            @PathVariable("webhook_id") Long webHookId) {
        return new ResponseEntity<>(webHookC7nService.queryById(webHookId), HttpStatus.OK);
    }


    @GetMapping("/records")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "查询WebHook发送记录(分页接口)")
    @CustomPageRequest
    public ResponseEntity<Page<WebhookRecordVO>> pageWebHookSendRecord(
            @ApiIgnore
            @SortDefault(value = "transaction_id", direction = Sort.Direction.DESC) PageRequest pageRequest,
            @PathVariable(name = "organization_id") Long sourceId,
            @Encrypt
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
            @Encrypt
            @PathVariable(name = "record_id") Long recordId) {
        return new ResponseEntity<>(webhookRecordC7nService.queryById(organizationId, recordId, ResourceLevel.ORGANIZATION.value()), HttpStatus.OK);
    }

    @ApiOperation(value = "组织层可用于创建webhook事件查询")
    @GetMapping("/send_settings")
    @Permission(level = ResourceLevel.ORGANIZATION)
    public ResponseEntity<WebHookVO.SendSetting> getTempServerForWebhook(
            @PathVariable("organization_id") Long organizationId,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "description", required = false) String description,
            @ApiParam(value = "webhook类型 DingTalk/WeChat/Json")
            @RequestParam(name = "type") String type) {
        return new ResponseEntity<>(sendSettingC7nService.getTempServerForWebhook(organizationId, ResourceLevel.ORGANIZATION.value(), name, description, type), HttpStatus.OK);
    }

    @ApiOperation(value = "接收webhook接口")
    @PostMapping("/receive")
    @Permission(permissionPublic = true)
    public ResponseEntity receive(
            @RequestBody String json) {
        LOGGER.info("===========================================================\n{}", json);
        return Results.success();
    }

}
