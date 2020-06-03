package io.choerodon.message.app.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.hzero.message.app.service.UserMessageService;
import org.hzero.message.domain.entity.UserMessage;
import org.hzero.message.domain.repository.UserMessageRepository;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
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

        // 查出用户的所有站内信
        List<UserMessage> userMessageList = userMessageRepository.selectByCondition(Condition.builder(UserMessage.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(UserMessage.FIELD_USER_ID, userId)
                ).build());

        // 这里面会调用将消息已读的逻辑
        userMessageService.deleteMessage(0L, userId, userMessageList.stream().map(UserMessage::getMessageId).collect(Collectors.toList()));
    }
}
