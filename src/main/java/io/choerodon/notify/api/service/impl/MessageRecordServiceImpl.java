package io.choerodon.notify.api.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.notify.api.dto.RecordListDTO;
import io.choerodon.notify.api.pojo.RecordQueryParam;
import io.choerodon.notify.api.pojo.RecordSendData;
import io.choerodon.notify.api.pojo.RecordStatus;
import io.choerodon.notify.api.service.MessageRecordService;
import io.choerodon.notify.api.service.NoticesSendService;
import io.choerodon.notify.domain.Record;
import io.choerodon.notify.domain.SendSetting;
import io.choerodon.notify.infra.mapper.RecordMapper;
import io.choerodon.notify.infra.mapper.SendSettingMapper;
import io.choerodon.notify.infra.mapper.TemplateMapper;
import io.choerodon.notify.infra.utils.ConvertUtils;
import org.springframework.stereotype.Service;

@Service
public class MessageRecordServiceImpl implements MessageRecordService {

    private final RecordMapper recordMapper;

    private final NoticesSendService noticesSendService;

    private final TemplateMapper templateMapper;

    private final SendSettingMapper settingMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public MessageRecordServiceImpl(RecordMapper recordMapper, NoticesSendService noticesSendService,
                                    TemplateMapper templateMapper,
                                    SendSettingMapper settingMapper) {
        this.recordMapper = recordMapper;
        this.noticesSendService = noticesSendService;
        this.templateMapper = templateMapper;
        this.settingMapper = settingMapper;
    }

    @Override
    public Page<RecordListDTO> pageEmail(final RecordQueryParam query) {
        return PageHelper.doPageAndSort(query.getPageRequest(), () ->
                recordMapper.fulltextSearchEmail(query));
    }

    @Override
    public Record manualRetrySendEmail(long recordId) {
        Record record = recordMapper.selectByPrimaryKey(recordId);
        if (record == null) {
            throw new CommonException("error.record.notExist");
        }
        SendSetting sendSetting = settingMapper.selectOne(new SendSetting(record.getBusinessType()));
        if (sendSetting == null) {
            throw new CommonException("error.noticeSend.codeNotFound");
        }
        if (!RecordStatus.FAILED.getValue().equals(record.getStatus())) {
            throw new CommonException("error.record.retryNotFailed");
        }
        if (sendSetting.getEmailTemplateId() == null) {
            throw new CommonException("error.noticeSend.emailTemplateNotSet");
        }
        io.choerodon.notify.domain.Template template = templateMapper.selectByPrimaryKey(sendSetting.getEmailTemplateId());
        if (template == null) {
            throw new CommonException("error.emailTemplate.notExist");
        }
        record.setSendData(new RecordSendData(template, ConvertUtils.convertJsonToMap(objectMapper, record.getVariables()),
                noticesSendService.createEmailSender(), sendSetting.getRetryCount()));
        noticesSendService.sendEmail(record, true);
        return recordMapper.selectByPrimaryKey(record.getId());
    }
}
