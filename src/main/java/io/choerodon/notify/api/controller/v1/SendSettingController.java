package io.choerodon.notify.api.controller.v1;

import io.choerodon.notify.api.service.SendSettingService;
import io.choerodon.notify.infra.dto.SendSettingDTO;
import io.choerodon.notify.infra.enums.SenderType;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author lrc
 * @since 2019/10/29
 */
@RestController
@RequestMapping("/v1/project/{project_id}/sendsetting")
public class SendSettingController {

    private SendSettingService sendSettingService;

    public SendSettingController(SendSettingService sendSettingService) {
        this.sendSettingService = sendSettingService;
    }

    @GetMapping
    @ApiOperation("查询项目层下的所有的SendSettingDTO")
    public ResponseEntity<List<SendSettingDTO>> getSendSettings(@PathVariable("project_id") Long projectId){
        return new ResponseEntity<>(sendSettingService.selectSendSetting(), HttpStatus.OK);
    }
}
