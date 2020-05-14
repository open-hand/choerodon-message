package io.choerodon.message.api.controller.v1;

import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.message.api.vo.StackingHistogramVO;
import io.choerodon.message.app.service.MailingRecordService;
import io.choerodon.swagger.annotation.Permission;
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
@RequestMapping("/choerodon/v1/mails/records")
public class MailendRecordController {

    private MailingRecordService mailingRecordService;

    public MailendRecordController(MailingRecordService mailingRecordService) {
        this.mailingRecordService = mailingRecordService;
    }

    @Permission(level = ResourceLevel.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    @GetMapping("/count_by_date")
    @ApiOperation(value = "按时间段统计邮件发送成功、失败次数(堆叠柱状图使用)")
    public ResponseEntity<StackingHistogramVO> countByDate(@RequestParam(value = "start_time") Date startTime,
                                                           @RequestParam(value = "end_time") Date endTime) {
        return ResponseEntity.ok(mailingRecordService.countByDate(startTime, endTime));
    }
}
