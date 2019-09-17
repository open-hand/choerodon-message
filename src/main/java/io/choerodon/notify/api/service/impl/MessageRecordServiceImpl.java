package io.choerodon.notify.api.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import io.choerodon.base.domain.*;
import io.choerodon.core.exception.*;
import io.choerodon.notify.api.dto.*;
import io.choerodon.notify.api.pojo.*;
import io.choerodon.notify.api.service.*;
import io.choerodon.notify.domain.*;
import io.choerodon.notify.infra.mapper.*;
import io.choerodon.notify.infra.utils.*;

@Service
public class MessageRecordServiceImpl implements MessageRecordService {

    private final RecordMapper recordMapper;

    private final EmailSendService emailSendService;

    private final TemplateMapper templateMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public MessageRecordServiceImpl(RecordMapper recordMapper,
                                    EmailSendService emailSendService,
                                    TemplateMapper templateMapper) {
        this.recordMapper = recordMapper;
        this.emailSendService = emailSendService;
        this.templateMapper = templateMapper;
    }

    @Override
    public PageInfo<RecordListDTO> pageEmail(String status, String receiveEmail, String templateType, String failedReason, String params, PageRequest pageRequest, String level) {
        return PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize()).doSelectPageInfo(() -> recordMapper.fulltextSearchEmail(status, receiveEmail, templateType, failedReason, params, level));
    }

    @Override
    public Record manualRetrySendEmail(long recordId) {
        Record record = recordMapper.selectByPrimaryKey(recordId);
        if (record == null) {
            throw new CommonException("error.record.notExist");
        }
        if (!RecordStatus.FAILED.getValue().equals(record.getStatus())) {
            throw new CommonException("error.record.retryNotFailed");
        }

        io.choerodon.notify.domain.Template template = templateMapper.selectByPrimaryKey(record.getTemplateId());
        if (template == null) {
            throw new CommonException("error.emailTemplate.notExist");
        }
        record.setSendData(new RecordSendData(template, ConvertUtils.convertJsonToMap(objectMapper, record.getVariables()),
                emailSendService.createEmailSender(), null));
        emailSendService.sendRecord(record, true);
        return recordMapper.selectByPrimaryKey(record.getId());
    }
}
