package io.choerodon.notify.api.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.notify.api.dto.RecordListDTO;
import io.choerodon.notify.api.service.MessageRecordService;
import io.choerodon.notify.infra.mapper.RecordMapper;
import org.springframework.stereotype.Service;

@Service
public class MessageRecordServiceImpl implements MessageRecordService {

    private RecordMapper recordMapper;

    public MessageRecordServiceImpl(RecordMapper recordMapper) {
        this.recordMapper = recordMapper;
    }

    @Override
    public Page<RecordListDTO> pageEmail(PageRequest pageRequest, String status,
                                         String receiveEmail, String templateType,
                                         String failedReason, String params) {
        return PageHelper.doPageAndSort(pageRequest, () ->
                recordMapper.fulltextSearchEmail(status, receiveEmail, templateType, failedReason, params));
    }
}
