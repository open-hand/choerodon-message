package io.choerodon.message.app.service;

import org.hzero.message.domain.entity.SmsServer;

import io.choerodon.message.api.vo.EmailConfigVO;

/**
 * @author scp
 * @date 2020/4/28
 * @description
 */
public interface ConfigC7nService {

    EmailConfigVO createOrUpdateEmail(EmailConfigVO emailConfigVO);

    EmailConfigVO selectEmail();

    void testEmailConnect();

    SmsServer createOrUpdateSmsServer(SmsServer smsServer);

    SmsServer selectSms();


}
