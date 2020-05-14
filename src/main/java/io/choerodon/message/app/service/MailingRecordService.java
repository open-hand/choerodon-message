package io.choerodon.message.app.service;

import io.choerodon.message.api.vo.StackingHistogramVO;

import java.util.Date;

/**
 * 〈功能简述〉
 * 〈邮件发送记录接口〉
 *
 * @author wanghao
 * @Date 2020/2/24 21:14
 */
public interface MailingRecordService {


    /**
     * 根据时间段统计邮件发送成功和失败的次数
     *
     * @param startTime
     * @param endTime
     * @return StackingHistogramVO 供堆叠柱状图渲染使用
     */
    StackingHistogramVO countByDate(Date startTime, Date endTime);
}
