package io.choerodon.notify.api.controller.v1;

import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.notify.api.service.MailingRecordService;
import io.choerodon.notify.api.vo.StackingHistogramVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 〈功能简述〉
 * 〈邮件发送记录相关操作Controller〉
 *
 * @author wanghao
 * @Date 2020/2/24 17:55
 */
@RestController
@RequestMapping("/v1/mails/records")
public class MailendRecordController {

    private MailingRecordService mailingRecordService;

    public MailendRecordController(MailingRecordService mailingRecordService) {
        this.mailingRecordService = mailingRecordService;
    }

    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    @GetMapping("/count_by_date")
    @ApiOperation(value = "按时间段统计邮件发送成功、失败次数(堆叠柱状图使用)")
    public ResponseEntity<StackingHistogramVO> countByDate(@RequestParam(value = "start_time") Date startTime,
                                                           @RequestParam(value = "end_time") Date endTime) {
        return ResponseEntity.ok(mailingRecordService.countByDate(startTime, endTime));
    }
}
