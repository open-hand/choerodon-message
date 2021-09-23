package io.choerodon.message.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.message.api.vo.CustomEmailSendInfoVO;
import io.choerodon.message.infra.dto.MessageC7nDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.springframework.web.multipart.MultipartFile;

public interface MessageC7nService {
    Page<MessageC7nDTO> listMessage(String status,
                                    String messageName,
                                    String params,
                                    PageRequest pageRequest);

    Page<MessageC7nDTO> listWebHooks(String status,
                                     String webhookAddress,
                                     String templateType,
                                     String failedReason,
                                     String params,
                                     PageRequest pageRequest);

    /**
     * 发送自定义邮件
     *
     * @param customEmailSendInfoVO
     */
    void sendCustomEmail(CustomEmailSendInfoVO customEmailSendInfoVO);

    /**
     * 获取前一小时发送失败的邮件
     * 并重新发送
     */

    void resendFailedEmail();
}