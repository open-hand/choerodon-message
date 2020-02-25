package io.choerodon.notify.api.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.notify.api.service.MailingRecordService;
import io.choerodon.notify.api.vo.StackingHistogramVO;
import io.choerodon.notify.infra.dto.MailingRecordDTO;
import io.choerodon.notify.infra.enums.EmailSendStatusEnum;
import io.choerodon.notify.infra.mapper.MailingRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @Date 2020/2/24 21:18
 */
@Service
public class MailingRecordServiceImpl implements MailingRecordService {

    private MailingRecordMapper mailingRecordMapper;

    public MailingRecordServiceImpl(MailingRecordMapper mailingRecordMapper) {
        this.mailingRecordMapper = mailingRecordMapper;
    }

    @Override
    public StackingHistogramVO countByDate(Date startTime, Date endTime) {
        if (startTime.after(endTime)) {
            throw new CommonException("error.invalid.param");
        }
        StackingHistogramVO stackingHistogramVO = new StackingHistogramVO();
        List<MailingRecordDTO> mailingRecordDTOS =  mailingRecordMapper.selectByDate(
                new java.sql.Date(startTime.getTime()),
                new java.sql.Date(endTime.getTime()));
        // 根据日期分组
        Map<String, List<MailingRecordDTO>> dateListMap = mailingRecordDTOS.stream()
                .collect(Collectors.groupingBy(t -> new java.sql.Date(t.getCreationDate().getTime()).toString()));

        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate startDate = startTime.toInstant().atZone(zoneId).toLocalDate();
        LocalDate endDate = endTime.toInstant().atZone(zoneId).toLocalDate();

        while (startDate.isBefore(endDate) || startDate.isEqual(endDate)) {
            String date = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            long countSuccessNum = 0;
            long countFailedNum = 0;
            // 计算成功发送的邮件数
            List<MailingRecordDTO> recordDTOS = dateListMap.get(date);
            if (!CollectionUtils.isEmpty(recordDTOS)) {
                countSuccessNum = recordDTOS.stream().filter(recordDTO -> EmailSendStatusEnum.COMPLETED.value().equals(recordDTO.getStatus())).count();
                countFailedNum = recordDTOS.stream().filter(recordDTO -> EmailSendStatusEnum.FAILED.value().equals(recordDTO.getStatus())).count();
            }
            stackingHistogramVO.getDates().add(date);
            stackingHistogramVO.getSuccessNums().add(countSuccessNum);
            stackingHistogramVO.getFailedNums().add(countFailedNum);
            stackingHistogramVO.getTotalNums().add(countSuccessNum + countFailedNum);

            startDate = startDate.plusDays(1);
        }
        return stackingHistogramVO;
    }
}
