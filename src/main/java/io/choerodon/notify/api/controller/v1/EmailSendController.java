package io.choerodon.notify.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/notices/emails")
public class EmailSendController {

    @PostMapping
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "发送邮件")
    public ResponseEntity<String> create() {
        return new ResponseEntity<>("hello world", HttpStatus.OK);
    }


}
