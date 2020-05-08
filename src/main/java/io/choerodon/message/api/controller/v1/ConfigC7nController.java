package io.choerodon.message.api.controller.v1;


import static io.choerodon.message.api.vo.EmailConfigVO.EMAIL_REGULAR_EXPRESSION;

import java.util.regex.Pattern;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.message.domain.entity.SmsServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.message.api.vo.EmailConfigVO;
import io.choerodon.message.app.service.ConfigC7nService;
import io.choerodon.swagger.annotation.Permission;

;

@RestController
@RequestMapping("v1/notices/configs")
@Api("邮箱，短信，站内信配置接口")
public class ConfigC7nController {
    @Autowired
    private ConfigC7nService configC7nService;

    @PutMapping("/email")
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "更新邮箱配置")
    public ResponseEntity<EmailConfigVO> updateEmail(@RequestBody EmailConfigVO emailConfigVO) {
        if (!StringUtils.isEmpty(emailConfigVO.getAccount()) && !Pattern.matches(EMAIL_REGULAR_EXPRESSION, emailConfigVO.getAccount())) {
            throw new CommonException("error.emailConfig.accountIllegal");
        }
        if (emailConfigVO.getPort() == null) {
            emailConfigVO.setPort(25);
        }
        return new ResponseEntity<>(configC7nService.createOrUpdateEmail(emailConfigVO), HttpStatus.OK);
    }

    @GetMapping("/email")
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "查询邮箱配置")
    public ResponseEntity<EmailConfigVO> selectEmail() {
        return new ResponseEntity<>(configC7nService.selectEmail(), HttpStatus.OK);
    }

    @GetMapping("/email/test")
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "测试邮箱连接")
    public void testEmailConnect(){
        configC7nService.testEmailConnect();
    }


    @PutMapping("/sms")
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "更新短信配置")
    public ResponseEntity<SmsServer> updateSms(@RequestBody SmsServer smsServer) {
        return new ResponseEntity<>(configC7nService.createOrUpdateSmsServer(smsServer), HttpStatus.OK);
    }

    @GetMapping("/sms")
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "查询短信配置")
    public ResponseEntity<SmsServer> selectSms() {
        return new ResponseEntity<>(configC7nService.selectSms(), HttpStatus.OK);
    }

}
