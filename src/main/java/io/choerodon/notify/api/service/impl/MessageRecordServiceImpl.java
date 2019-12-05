package io.choerodon.notify.api.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ext.NotExistedException;
import io.choerodon.notify.api.dto.RecordListDTO;
import io.choerodon.notify.api.pojo.RecordSendData;
import io.choerodon.notify.api.service.EmailSendService;
import io.choerodon.notify.api.service.MessageRecordService;
import io.choerodon.notify.domain.Record;
import io.choerodon.notify.infra.dto.MailingRecordDTO;
import io.choerodon.notify.infra.dto.Template;
import io.choerodon.notify.infra.enums.RecordStatus;
import io.choerodon.notify.infra.mapper.MailingRecordMapper;
import io.choerodon.notify.infra.mapper.TemplateMapper;
import io.choerodon.notify.infra.utils.ConvertUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MessageRecordServiceImpl implements MessageRecordService {

    private final MailingRecordMapper mailingRecordMapper;

    private final EmailSendService emailSendService;

    private final TemplateMapper templateMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public MessageRecordServiceImpl(MailingRecordMapper mailingRecordMapper,
                                    EmailSendService emailSendService,
                                    TemplateMapper templateMapper) {
        this.mailingRecordMapper = mailingRecordMapper;
        this.emailSendService = emailSendService;
        this.templateMapper = templateMapper;
    }

    @Override
    public PageInfo<RecordListDTO> pageEmail(String status, String receiveEmail, String templateType, String failedReason, String params, Pageable pageable, String level) {
        return PageHelper.startPage(pageable.getPageNumber(), pageable.getPageSize()).doSelectPageInfo(() -> mailingRecordMapper.fulltextSearchEmail(status, receiveEmail, templateType, failedReason, params, level));
    }

    @Override
    public Record manualRetrySendEmail(long recordId) {
        Record record = getRecord(recordId);
        if (!RecordStatus.FAILED.getValue().equals(record.getStatus())) {
            throw new CommonException("error.record.retryNotFailed");
        }

        Template template = templateMapper.selectByPrimaryKey(record.getTemplateId());
        if (template == null) {
            throw new CommonException("error.emailTemplate.notExist");
        }
        record.setSendData(new RecordSendData(template, ConvertUtils.convertJsonToMap(objectMapper, record.getVariables()),
                emailSendService.createEmailSender(), null));
        emailSendService.sendRecord(record, true);
        return getRecord(record.getId());
    }

    private Record getRecord(Long recordId) {
        Record record = new Record();
        MailingRecordDTO mailingRecordDTO = Optional.ofNullable(mailingRecordMapper.selectByPrimaryKey(recordId)).orElseThrow(() -> new NotExistedException("error.mailing.record.does.not.existed"));
        BeanUtils.copyProperties(mailingRecordDTO, record);
        return record;
    }
}
