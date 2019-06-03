package io.choerodon.notify.infra.utils;

import io.choerodon.notify.api.dto.NoticeSendDTO;
import io.choerodon.notify.api.service.NoticesSendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

@Service
public class AsyncSendAnnouncementUtils {
    private static final Logger logger = LoggerFactory.getLogger(AsyncSendAnnouncementUtils.class);

    private NoticesSendService noticesSendService;

    public AsyncSendAnnouncementUtils(NoticesSendService noticesSendService) {
        this.noticesSendService = noticesSendService;
    }

    /**
     * 异步
     * 向用户发送通知（包括邮件和站内信）
     *
     * @param fromUserId 发送通知的用户
     * @param userIds    接受通知的目标用户
     * @param code       业务code
     * @param params     渲染参数
     * @param sourceId   触发发送通知对应的组织/项目id，如果是site层，可以为0或null
     */
    @Async("notify-executor")
    public Future<String> sendNoticeToAll(Long fromUserId, List<Long> userIds, String code, Map<String, Object> params, Long sourceId) {
        logger.debug("ready : send Notice:'{}' to " + userIds.size() + " users.", code);
        if (userIds == null || userIds.isEmpty()) return new AsyncResult<>("userId is null");
        long beginTime = System.currentTimeMillis();
        NoticeSendDTO noticeSendDTO = new NoticeSendDTO();
        noticeSendDTO.setCode(code);
        NoticeSendDTO.User currentUser = new NoticeSendDTO.User();
        currentUser.setId(fromUserId);
        noticeSendDTO.setFromUser(currentUser);
        noticeSendDTO.setParams(params);
        noticeSendDTO.setSourceId(sourceId);
        List<NoticeSendDTO.User> users = new LinkedList<>();
        userIds.forEach(id -> {
            NoticeSendDTO.User user = new NoticeSendDTO.User();
            user.setId(id);
            users.add(user);
        });
        noticeSendDTO.setTargetUsers(users);
        logger.debug("start : send Notice:'{}' to " + users.size() + " users.", code);
        noticesSendService.sendNotice(noticeSendDTO);
        logger.debug("end : send Notice:'{}' to " + userIds.size() + " users.", code);
        return new AsyncResult<>((System.currentTimeMillis() - beginTime) / 1000 + "s");
    }
}
