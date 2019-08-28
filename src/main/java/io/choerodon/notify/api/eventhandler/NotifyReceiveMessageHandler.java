package io.choerodon.notify.api.eventhandler;

import static io.choerodon.notify.api.service.impl.WebSocketWsSendServiceImpl.MSG_TYPE_PM;

import java.util.Map;

import io.choerodon.notify.infra.utils.OnlineCountStorageUtils;
import io.choerodon.websocket.helper.WebSocketHelper;
import io.choerodon.websocket.receive.TextMessageHandler;
import io.choerodon.websocket.send.SendMessagePayload;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketSession;

import io.choerodon.notify.infra.mapper.SiteMsgRecordMapper;

@Primary
@Component
public class NotifyReceiveMessageHandler implements TextMessageHandler<String> {

    private WebSocketHelper webSocketHelper;

    private OnlineCountStorageUtils onlineCountStorageUtils;

    private final SiteMsgRecordMapper siteMsgRecordMapper;

    private static final String SIT_MSG_KEY_PATH = "choerodon:msg:{code}:{id}";
    public static final String ONLINE_INFO_KEY_PATH = "choerodon:msg:online-info";

    private static final String SITE_MSG_CODE = "site-msg";
    public static final String ONLINE_INFO_CODE = "online-info";

    private final AntPathMatcher matcher = new AntPathMatcher();

    public NotifyReceiveMessageHandler(@Lazy WebSocketHelper webSocketHelper,
                                       OnlineCountStorageUtils onlineCountStorageUtils, SiteMsgRecordMapper siteMsgRecordMapper) {
        this.webSocketHelper = webSocketHelper;
        this.onlineCountStorageUtils = onlineCountStorageUtils;
        this.siteMsgRecordMapper = siteMsgRecordMapper;
    }

    @Override
    public String matchType() {
        return "notify";
    }

    @Override
    public void handle(WebSocketSession session, String type, String key, String payload) {
        if (!StringUtils.isEmpty(key)) {
            webSocketHelper.subscribe(key, session);
            webSocketHelper.sendMessageBySession(session, new SendMessagePayload<>("contact", key, "ok"));
        }
        if (matcher.match(SIT_MSG_KEY_PATH, key)) {
            Map<String, String> map = matcher.extractUriTemplateVariables(SIT_MSG_KEY_PATH, key);
            String code = map.get("code");
            if (SITE_MSG_CODE.equals(code)) {
                String id = map.get("id");
                if (id != null) {
                    int unReadNum = siteMsgRecordMapper.selectCountOfUnRead(Long.parseLong(id));
                    if (unReadNum > 0) {
                        webSocketHelper.sendMessageBySession(session, new SendMessagePayload<>(MSG_TYPE_PM, key, unReadNum));
                    }
                }
            }
        } else if (matcher.match(ONLINE_INFO_KEY_PATH, key)) {
            webSocketHelper.sendMessageBySession(session, new SendMessagePayload<>(ONLINE_INFO_CODE, ONLINE_INFO_KEY_PATH, onlineCountStorageUtils.makeVisitorsInfo()));
        }
    }
}
