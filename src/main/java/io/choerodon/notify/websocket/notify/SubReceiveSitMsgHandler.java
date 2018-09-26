package io.choerodon.notify.websocket.notify;


import io.choerodon.notify.infra.mapper.SiteMsgRecordMapper;
import io.choerodon.notify.websocket.receive.ReceiveMsgHandler;
import io.choerodon.notify.websocket.relationship.RelationshipDefining;
import io.choerodon.notify.websocket.send.MessageSender;
import io.choerodon.notify.websocket.send.WebSocketSendPayload;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

import static io.choerodon.notify.api.service.impl.WebSocketWsSendServiceImpl.MSG_TYPE_PM;

@Component
public class SubReceiveSitMsgHandler implements ReceiveMsgHandler<String> {

    private static final String SUB = "sub";

    private RelationshipDefining relationshipDefining;

    private MessageSender messageSender;

    private final SiteMsgRecordMapper siteMsgRecordMapper;

    private static final String SIT_MSG_KEY_PATH = "choerodon:msg:{code}:{id}";

    private final AntPathMatcher matcher = new AntPathMatcher();

    public SubReceiveSitMsgHandler(RelationshipDefining relationshipDefining,
                                   MessageSender messageSender,
                                   SiteMsgRecordMapper siteMsgRecordMapper) {
        this.relationshipDefining = relationshipDefining;
        this.messageSender = messageSender;
        this.siteMsgRecordMapper = siteMsgRecordMapper;
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
            String id = map.get("id");
            if (id != null) {
                int unReadNum = siteMsgRecordMapper.selectCountOfUnRead(Long.parseLong(id));
                if (unReadNum > 0) {
                    messageSender.sendWebSocket(session, new WebSocketSendPayload<>(MSG_TYPE_PM, key, unReadNum));
                }
            }
        }
    }

}
