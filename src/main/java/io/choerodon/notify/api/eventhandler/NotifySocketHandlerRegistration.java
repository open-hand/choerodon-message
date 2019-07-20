package io.choerodon.notify.api.eventhandler;

import io.choerodon.notify.infra.utils.OnlineCountStorageUtils;
import io.choerodon.websocket.helper.SocketHandlerRegistration;
import io.choerodon.websocket.helper.WebSocketHelper;
import io.choerodon.websocket.send.WebSocketSendPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class NotifySocketHandlerRegistration implements SocketHandlerRegistration {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotifySocketHandlerRegistration.class);

    private WebSocketHelper webSocketHelper;
    private RestTemplate restTemplate;
    private OnlineCountStorageUtils onlineCountStorageUtils;

    public NotifySocketHandlerRegistration(WebSocketHelper webSocketHelper, RestTemplate restTemplate, OnlineCountStorageUtils onlineCountStorageUtils) {
        this.webSocketHelper = webSocketHelper;
        this.restTemplate = restTemplate;
        this.onlineCountStorageUtils = onlineCountStorageUtils;
    }

    @Override
    public String path() {
        return "/choerodon/msg";
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        try {
            MultiValueMap<String, String> parameters = UriComponentsBuilder.fromUri(serverHttpRequest.getURI()).build().getQueryParams();
            String accessToken = parameters.getFirst("token");
            if (accessToken != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.set(HttpHeaders.AUTHORIZATION, "bearer " + accessToken);
                HttpEntity<String> entity = new HttpEntity<>(headers);
                ResponseEntity responseEntity = restTemplate.exchange("http://oauth-server/oauth/api/user", HttpMethod.GET, entity, String.class);
                if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null){
                    JSONObject object = new JSONObject(responseEntity.getBody().toString());
                    serverHttpRequest.getHeaders().add("X-WebSocket-UserName", object.getJSONObject("principal").getString("username"));
                    serverHttpRequest.getHeaders().add("X-WebSocket-UserID", object.getJSONObject("principal").getString("userId"));
                    return true;
                } else {
                    LOGGER.warn("reject webSocket connect, redirect request to oauth-server not 2xx");
                    return false;
                }
            } else {
                LOGGER.warn("reject webSocket connect, header must have 'Authorization' access-token");
                return false;
            }
        } catch (RestClientException e) {
            LOGGER.error("reject webSocket connect, redirect request to oauth-server error", e);
            return false;
        } catch (JSONException e) {
            LOGGER.error("reject webSocket connect, oauth-server response json error", e);
            return false;
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String userId = session.getHandshakeHeaders().getFirst("X-WebSocket-UserID");
        //今日访问人数+1，在线人数+1,发送在线信息
        LOGGER.info("Get sitMsg's subscription,sessionId: {}", session.getId());
        Integer originCount = onlineCountStorageUtils.getOnlineCount();
        onlineCountStorageUtils.addNumberOfVisitorsToday(userId);
        onlineCountStorageUtils.addOnlineCount(userId, session.getId());
        if (!originCount.equals(onlineCountStorageUtils.getOnlineCount())) {
            onlineCountStorageUtils.makeVisitorsInfo();
            webSocketHelper.sendMessage(NotifyReceiveMessageHandler.ONLINE_INFO_KEY_PATH, new WebSocketSendPayload<>(NotifyReceiveMessageHandler.ONLINE_INFO_CODE, NotifyReceiveMessageHandler.ONLINE_INFO_KEY_PATH, onlineCountStorageUtils.makeVisitorsInfo()));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        String userId = session.getHandshakeHeaders().getFirst("X-WebSocket-UserID");
        Integer originCount = onlineCountStorageUtils.getOnlineCount();
        onlineCountStorageUtils.subOnlineCount(userId, session.getId());
        if (!originCount.equals(onlineCountStorageUtils.getOnlineCount())) {
            onlineCountStorageUtils.makeVisitorsInfo();
            webSocketHelper.sendMessage(NotifyReceiveMessageHandler.ONLINE_INFO_KEY_PATH, new WebSocketSendPayload<>(NotifyReceiveMessageHandler.ONLINE_INFO_CODE, NotifyReceiveMessageHandler.ONLINE_INFO_KEY_PATH, onlineCountStorageUtils.makeVisitorsInfo()));
        }
    }
}
