package io.choerodon.message.app.service.impl;

import java.util.Objects;

import org.hzero.message.app.service.UserMessageService;
import org.hzero.message.domain.entity.UserMessage;
import org.hzero.message.domain.repository.UserMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.message.app.service.C7nMessageService;

/**
 * @author zmf
 * @since 2020/6/3
 */
@Service
public class C7nMessageServiceImpl implements C7nMessageService {
    @Autowired
    private UserMessageRepository userMessageRepository;
    @Autowired
    private UserMessageService userMessageService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAllSiteMessages() {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        UserMessage condition = new UserMessage();
        condition.setUserId(Objects.requireNonNull(userId));

        // 将所有消息已读
        userMessageService.readMessage(0L, userId);

        // 删除用户的所有信息
        userMessageRepository.delete(condition);
    }
}
