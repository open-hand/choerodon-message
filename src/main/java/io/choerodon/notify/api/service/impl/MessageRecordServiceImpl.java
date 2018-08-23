package io.choerodon.notify.api.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.notify.api.dto.RecordListDTO;
import io.choerodon.notify.api.service.MessageRecordService;
import io.choerodon.notify.api.service.NoticesSendService;
import io.choerodon.notify.domain.Config;
import io.choerodon.notify.domain.Record;
import io.choerodon.notify.domain.Template;
import io.choerodon.notify.infra.cache.ConfigCache;
import io.choerodon.notify.infra.mapper.RecordMapper;
import io.choerodon.notify.infra.mapper.TemplateMapper;
import io.choerodon.notify.infra.utils.ConvertUtils;
import org.springframework.stereotype.Service;

@Service
public class MessageRecordServiceImpl implements MessageRecordService {

    private final RecordMapper recordMapper;

    private final NoticesSendService noticesSendService;

    private final ConfigCache configCache;

    private final TemplateMapper templateMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public MessageRecordServiceImpl(RecordMapper recordMapper, NoticesSendService noticesSendService,
                                    ConfigCache configCache, TemplateMapper templateMapper) {
        this.recordMapper = recordMapper;
        this.noticesSendService = noticesSendService;
        this.configCache = configCache;
        this.templateMapper = templateMapper;
    }

    @Override
    public Page<RecordListDTO> pageEmail(PageRequest pageRequest, String status,
                                         String receiveEmail, String templateType,
                                         String failedReason, String params, String level) {
        return PageHelper.doPageAndSort(pageRequest, () ->
                recordMapper.fulltextSearchEmail(status, receiveEmail, templateType, failedReason, params, level));
    }

    @Override
    public void manualRetrySendEmail(long recordId) {
        Record record = recordMapper.selectByPrimaryKey(recordId);
        if (record == null) {
            throw new CommonException("error.record.notExist");
        }
        if (!Record.RecordStatus.FAILED.getValue().equals(record.getStatus())) {
            throw new CommonException("error.record.retryNotFailed");
        }
        Template template = templateMapper.selectByPrimaryKey(record.getTemplateId());
        if (template == null) {
            throw new CommonException("error.emailTemplate.notExist");
        }
        final Config config = configCache.getEmailConfig();
        record.setMailSender(noticesSendService.createEmailSender(config));
        record.setConfig(config);
        record.setTemplate(template);
        record.setVariablesMap(ConvertUtils.convertJsonToMap(objectMapper, record.getVariables()));
        noticesSendService.sendEmail(record, false);
    }
}
