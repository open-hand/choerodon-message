package io.choerodon.message.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.message.app.service.MessageC7nService;
import io.choerodon.message.infra.dto.MessageC7nDTO;
import io.choerodon.message.infra.mapper.MessageC7nMapper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.message.infra.mapper.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author lihao
 */
@Service
public class MessageC7nServiceImpl implements MessageC7nService {
    @Autowired
    private MessageC7nMapper messageC7nMapper;

    @Override
    public Page<MessageC7nDTO> listMessage(String status, String receiveEmail, String templateType, String failedReason, String params, PageRequest pageRequest) {
        return PageHelper.doPage(pageRequest, () -> messageC7nMapper.listMessage(status, receiveEmail, templateType, failedReason, params));
    }

    @Override
    public Page<MessageC7nDTO> listWebHooks(String status, String webhookAddress, String templateType, String failedReason, String params, PageRequest pageRequest) {
        return PageHelper.doPage(pageRequest, () -> messageC7nMapper.listWebHooks(status, webhookAddress, templateType, failedReason, params));
    }
}
