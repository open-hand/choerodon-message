package io.choerodon.message.app.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import io.choerodon.core.domain.Page;
import io.choerodon.message.api.vo.CustomEmailSendInfoVO;
import io.choerodon.message.api.vo.MessageTrxStatusVO;
import io.choerodon.message.infra.dto.MessageC7nDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

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

    void resendFailedEmail(Date endDate);

    List<MessageTrxStatusVO> queryTrxStatusCode(Set<String> userEmails, String templateCode);

    /**
     * 查询钉钉执行记录
     * @param organizationId
     * @param status
     * @param messageName
     * @param params
     * @param pageRequest
     * @return
     */
    Page<MessageC7nDTO> pageDingTalk(Long organizationId, String status, String messageName, String params, PageRequest pageRequest);
}