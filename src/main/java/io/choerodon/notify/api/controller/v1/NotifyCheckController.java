package io.choerodon.notify.api.controller.v1;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.core.annotation.Permission;
import io.choerodon.notify.api.service.NotifyCheckLogService;

@RestController
@RequestMapping(value = "/v1/upgrade")
public class NotifyCheckController {

    @Autowired
    private NotifyCheckLogService notifyCheckLogService;

    /**
     * 平滑升级
     *
     * @param version 版本
     */
    @Permission(permissionLogin = true)
    @ApiOperation(value = "平滑升级")
    @GetMapping
    public ResponseEntity<String> checkLog(
            @ApiParam(value = "version")
            @RequestParam(value = "version") String version) {
        notifyCheckLogService.checkLog(version);
        return new ResponseEntity<>(System.currentTimeMillis() + "", HttpStatus.OK);
    }

}
