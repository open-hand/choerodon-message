package io.choerodon.message.api.controller.v1;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.message.api.vo.CustomEmailSendInfoVO;
import io.choerodon.message.app.service.MessageC7nService;
import io.choerodon.swagger.annotation.Permission;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/12/15 16:17
 */
@RestController
@RequestMapping("/choerodon/v1/message/emails")
public class C7nMailSendController {

    @Autowired
    private MessageC7nService messageC7nService;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("发送自定义邮件")
    @PostMapping("/custom")
    public ResponseEntity<Void> sendCustomEmail(@RequestBody @Validated CustomEmailSendInfoVO customEmailSendInfoVO) {
        messageC7nService.sendCustomEmail(customEmailSendInfoVO);
        return ResponseEntity.noContent().build();
    }

}
