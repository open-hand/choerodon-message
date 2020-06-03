package io.choerodon.message.api.ws;

import java.util.Map;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.hzero.websocket.config.WebSocketConfig;
import org.hzero.websocket.constant.WebSocketConstant;
import org.hzero.websocket.interceptor.SocketInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author zmf
 * @since 20-5-11
 */
@Component
public class NotifyWebSocketInterceptor implements SocketInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotifyWebSocketInterceptor.class);

    private final RestTemplate restTemplate;
    private final WebSocketConfig webSocketConfig;

    public NotifyWebSocketInterceptor(RestTemplate restTemplate, WebSocketConfig webSocketConfig) {
        this.restTemplate = restTemplate;
        this.webSocketConfig = webSocketConfig;
    }

    @Override
    public String processor() {
        return "choerodon_msg";
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        MultiValueMap<String, String> parameters = UriComponentsBuilder.fromUri(serverHttpRequest.getURI()).build().getQueryParams();
        String accessToken = parameters.getFirst(WebSocketConstant.Attributes.TOKEN);
        String responseJson = null;
        try {
            if (accessToken != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.set(HttpHeaders.AUTHORIZATION, "bearer " + accessToken);
                HttpEntity<String> entity = new HttpEntity<>(headers);
                ResponseEntity<String> responseEntity = restTemplate.exchange(webSocketConfig.getOauthUrl(), HttpMethod.GET, entity, String.class);
                if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                    responseJson = responseEntity.getBody();
                    JSONObject object = JSONObject.parseObject(responseJson);
                    serverHttpRequest.getHeaders().add("X-WebSocket-UserName", object.getJSONObject("principal").getString("username"));
                    serverHttpRequest.getHeaders().add("X-WebSocket-UserID", object.getJSONObject("principal").getString("userId"));
                    return true;
                } else {
                    LOGGER.warn("reject webSocket connect, redirect request to {} not 2xx", webSocketConfig.getOauthUrl());
                    return false;
                }
            } else {
                LOGGER.warn("reject webSocket connect, must have 'token' query parameter");
                return false;
            }
        } catch (RestClientException e) {
            LOGGER.error("reject webSocket connect, redirect request to {} error. The token is {}.", webSocketConfig.getOauthUrl(), accessToken);
            return false;
        } catch (JSONException e) {
            LOGGER.error("reject webSocket connect, oauth-server response json error. the json is {}", responseJson, e);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
