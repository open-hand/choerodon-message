package io.choerodon.notify.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.notify.api.dto.WebhookRecordVO;
import io.choerodon.notify.api.service.WebhookRecordService;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author jiameng.cao
 * @date 2019/11/4
 */
@RestController
@RequestMapping(value = "/v1/web_hook_records")
public class WebhookRecordController {
    private WebhookRecordService webhookRecordService;

    public WebhookRecordController(WebhookRecordService webhookRecordService) {
        this.webhookRecordService = webhookRecordService;
    }

    @GetMapping
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "分页查询WebHook发送记录信息")
    @CustomPageRequest
    public ResponseEntity<PageInfo<WebhookRecordVO>> pagingByMessage(@ApiIgnore
                                                                     @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                                                     @RequestParam(required = false, name = "project_name") String projectName,
                                                                     @RequestParam(required = false) String status,
                                                                     @RequestParam(required = false, name = "send_setting_name") String sendSettingName,
                                                                     @RequestParam(required = false, name = "webhook_path") String webhookPath,
                                                                     @RequestParam(required = false) String params) {
        WebhookRecordVO webhookRecordVO = new WebhookRecordVO();
        if (!StringUtils.isEmpty(projectName)) {
            webhookRecordVO.setProjectName(projectName);
        }
        if (!StringUtils.isEmpty(status)) {
            webhookRecordVO.setStatus(status);
        }
        if (!StringUtils.isEmpty(sendSettingName)) {
            webhookRecordVO.setSendSettingName(sendSettingName);
        }
        if (!StringUtils.isEmpty(webhookPath)) {
            webhookRecordVO.setStatus(webhookPath);
        }
        return new ResponseEntity<>(webhookRecordService.pagingWebHookRecord(pageable, webhookRecordVO, params), HttpStatus.OK);
    }
}
