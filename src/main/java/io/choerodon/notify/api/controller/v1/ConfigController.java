package io.choerodon.notify.api.controller.v1;

import io.choerodon.core.annotation.Permission;;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.notify.api.dto.EmailConfigDTO;
import io.choerodon.notify.api.service.ConfigService;
import io.choerodon.notify.api.service.NoticesSendService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.regex.Pattern;

import static io.choerodon.notify.api.dto.EmailConfigDTO.EMAIL_REGULAR_EXPRESSION;
import static io.choerodon.notify.domain.Config.EMAIL_PROTOCOL_SMTP;

@RestController
@RequestMapping("v1/notices/configs")
@Api("邮箱，短信，站内信配置接口")
public class ConfigController {

    private ConfigService configService;

    private NoticesSendService noticesSendService;

    public ConfigController(ConfigService configService, NoticesSendService noticesSendService) {
        this.configService = configService;
        this.noticesSendService = noticesSendService;
    }

    @PostMapping("/email")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "创建邮箱配置")
    public ResponseEntity<EmailConfigDTO> createEmail(@RequestBody @Valid EmailConfigDTO config) {
        config.setObjectVersionNumber(null);
        if (config.getSsl() == null) {
            config.setSsl(false);
        }
        if (config.getProtocol() == null) {
            config.setProtocol(EMAIL_PROTOCOL_SMTP);
        }
        return new ResponseEntity<>(configService.create(config), HttpStatus.OK);
    }

    @PutMapping("/email")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "更新邮箱配置")
    public ResponseEntity<EmailConfigDTO> updateEmail(@RequestBody EmailConfigDTO configDTO) {
        if (!StringUtils.isEmpty(configDTO.getAccount()) && !Pattern.matches(EMAIL_REGULAR_EXPRESSION, configDTO.getAccount())) {
            throw new CommonException("error.emailConfig.accountIllegal");
        }
        if (configDTO.getObjectVersionNumber() == null) {
            throw new CommonException("error.emailConfig.objectVersionNumberNull");
        }
        if (configDTO.getPort() == null) {
            configDTO.setPort(25);
        }
        return new ResponseEntity<>(configService.update(configDTO), HttpStatus.OK);
    }

    @GetMapping("/email")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "查询邮箱配置")
    public ResponseEntity<EmailConfigDTO> selectEmail() {
        return new ResponseEntity<>(configService.selectEmail(), HttpStatus.OK);
    }

    @PostMapping("/email/test")
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "测试邮箱连接")
    public void testEmailConnect(@RequestBody @Valid EmailConfigDTO config) {
        if (config.getSsl() == null) {
            config.setSsl(false);
        }
        if (config.getProtocol() == null) {
            config.setProtocol(EMAIL_PROTOCOL_SMTP);
        }
        if (config.getPort() == null) {
            config.setPort(25);
        }
        noticesSendService.testEmailConnect(config);
    }

}
