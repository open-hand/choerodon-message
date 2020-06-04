package io.choerodon.message.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.message.infra.dto.MessageC7nDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.Date;

public interface MessageC7nService {
    Page<MessageC7nDTO> listMessage(Long tenantId,
                                    String serverCode,
                                    String messageTypeCode,
                                    String subject,
                                    String trxStatusCode,
                                    Date startDate,
                                    Date endDate,
                                    String receiver,
                                    PageRequest pageRequest);
}
