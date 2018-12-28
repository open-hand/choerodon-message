package io.choerodon.notify.websocket.notify;

import static io.choerodon.notify.api.service.impl.WebSocketWsSendServiceImpl.MSG_TYPE_PM;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketSession;

import io.choerodon.notify.api.service.WebSocketSendService;
import io.choerodon.notify.infra.mapper.SiteMsgRecordMapper;
import io.choerodon.notify.websocket.receive.ReceiveMsgHandler;
import io.choerodon.notify.websocket.relationship.DefaultRelationshipDefining;
import io.choerodon.notify.websocket.relationship.RelationshipDefining;
import io.choerodon.notify.websocket.send.MessageSender;
import io.choerodon.notify.websocket.send.WebSocketSendPayload;

@Component
public class SubReceiveMessageHandler implements ReceiveMsgHandler<String> {
    private static final String SUB = "sub";
    private static final Logger LOGGER = LoggerFactory.getLogger(SubReceiveMessageHandler.class);

    private RelationshipDefining relationshipDefining;
    private WebSocketSendService webSocketSendService;

    private MessageSender messageSender;

    private final SiteMsgRecordMapper siteMsgRecordMapper;

    private static final String SIT_MSG_KEY_PATH = "choerodon:msg:{code}:{id}";

    private static final String SITE_MSG_CODE = "site-msg";

    private final AntPathMatcher matcher = new AntPathMatcher();
    private DefaultRelationshipDefining defaultRelationshipDefining;

    public SubReceiveMessageHandler(RelationshipDefining relationshipDefining,
                                    MessageSender messageSender,
                                    SiteMsgRecordMapper siteMsgRecordMapper,
                                    WebSocketSendService webSocketSendService,
                                    DefaultRelationshipDefining defaultRelationshipDefining) {
        this.relationshipDefining = relationshipDefining;
        this.messageSender = messageSender;
        this.siteMsgRecordMapper = siteMsgRecordMapper;
        this.webSocketSendService = webSocketSendService;
        this.defaultRelationshipDefining = defaultRelationshipDefining;
    }

    @Override
    public String matchType() {
        return SUB;
    }

    @Override
    public void handle(WebSocketSession session, String key) {
        if (!StringUtils.isEmpty(key)) {
            relationshipDefining.contact(key, session);
            messageSender.sendWebSocket(session, new WebSocketSendPayload<>(SUB, null, relationshipDefining.getKeysBySession(session)));
        }
        if (matcher.match(SIT_MSG_KEY_PATH, key)) {
            Map<String, String> map = matcher.extractUriTemplateVariables(SIT_MSG_KEY_PATH, key);
            String code = map.get("code");
            if (SITE_MSG_CODE.equals(code)) {
                String id = map.get("id");
                if (id != null) {
                    int unReadNum = siteMsgRecordMapper.selectCountOfUnRead(Long.parseLong(id));
                    if (unReadNum > 0) {
                        messageSender.sendWebSocket(session, new WebSocketSendPayload<>(MSG_TYPE_PM, key, unReadNum));
                    }
                    //今日访问人数+1，在线人数+1,发送在线信息
                    LOGGER.info("Get sitMsg's subscription,sessionId: {}", session.getId());
                    defaultRelationshipDefining.addNumberOfVisitorsToday(id);
                    defaultRelationshipDefining.addOnlineCount(id, session.getId());
                    webSocketSendService.sendVisitorsInfo(defaultRelationshipDefining.getOnlineCount(),
                            defaultRelationshipDefining.getNumberOfVisitorsToday());
                }
            }
        }
    }

}
