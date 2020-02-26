package io.choerodon.notify.api.controller.v1;

import java.util.HashMap;
import java.util.Map;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.notify.infra.utils.OnlineCountStorageUtils;

@RestController
@RequestMapping(value = "/v1/online")
public class OnlineCountController {
    @Autowired
    private OnlineCountStorageUtils onlineCountStorageUtils;

    /**
     * 当前在线人数和访问人数
     */
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_DEVELOPER})
    @ApiOperation(value = "当前在线人数和访问人数")
    @GetMapping(value = "/current", produces = "application/json")
    public ResponseEntity<Map> getCurrentCount() {
        return new ResponseEntity<>(onlineCountStorageUtils.getCurrentCount(), HttpStatus.OK);
    }

    /**
     * 每小时在线人数统计
     */
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_DEVELOPER})
    @ApiOperation(value = "每小时在线人数统计")
    @GetMapping(value = "/current/list", produces = "application/json")
    public ResponseEntity<Map> getCurrentCountPerHour() {
        return new ResponseEntity<>(onlineCountStorageUtils.getCurrentCountPerHour(), HttpStatus.OK);
    }
}
