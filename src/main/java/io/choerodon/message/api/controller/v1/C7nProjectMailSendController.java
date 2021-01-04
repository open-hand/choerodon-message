package io.choerodon.message.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.message.api.vo.CustomEmailSendInfoVO;
import io.choerodon.message.app.service.MessageC7nService;
import io.choerodon.message.infra.config.C7nSwaggerApiConfig;
import io.choerodon.swagger.annotation.Permission;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/12/15 16:17
 */
@RestController
@Api(tags = C7nSwaggerApiConfig.CHOEROODN_USER_MESSAGES)
@RequestMapping("/choerodon/v1/projects/{project_id}/message/emails")
public class C7nProjectMailSendController {

    @Autowired
    private MessageC7nService messageC7nService;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("发送自定义邮件")
    @PostMapping("/custom")
    public ResponseEntity<Void> sendCustomEmail(@RequestBody @Validated CustomEmailSendInfoVO customEmailSendInfoVO,
                                                @RequestParam(value = "file", required = false) MultipartFile file) {
        messageC7nService.sendCustomEmail(customEmailSendInfoVO, file);
        return ResponseEntity.noContent().build();
    }

}
