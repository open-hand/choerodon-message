package io.choerodon.message.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.message.app.service.C7nMessageService;
import io.choerodon.message.infra.config.C7nSwaggerApiConfig;

/**
 * @author zmf
 * @since 2020/6/3
 */
@Api(tags = C7nSwaggerApiConfig.CHOEROODN_USER_MESSAGES)
@RestController
@RequestMapping("/choerodon/v1/messages")
public class C7nMessageController {
    @Autowired
    private C7nMessageService c7nMessageService;

    @ApiOperation("彻底删除用户当前的所有站内信")
    @DeleteMapping("/user/delete_all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearAll() {
        c7nMessageService.deleteAllSiteMessages();
    }
}
