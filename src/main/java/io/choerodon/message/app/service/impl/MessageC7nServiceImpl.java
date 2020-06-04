package io.choerodon.message.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.message.app.service.MessageC7nService;
import io.choerodon.message.infra.dto.MessageC7nDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author lihao
 */
@Service
public class MessageC7nServiceImpl implements MessageC7nService {
    @Override
    public Page<MessageC7nDTO> listMessage(Long tenantId, String serverCode, String messageTypeCode, String subject, String trxStatusCode, Date startDate, Date endDate, String receiver, PageRequest pageRequest) {
        return null;
    }
}
