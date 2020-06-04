package io.choerodon.message.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.message.infra.dto.MessageC7nDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.Date;

import org.springframework.data.domain.Pageable;

public interface MessageC7nService {
    Page<MessageC7nDTO> listMessage(String status,
                                    String receiveEmail,
                                    String templateType,
                                    String failedReason,
                                    String params,
                                    PageRequest pageRequest);
}
