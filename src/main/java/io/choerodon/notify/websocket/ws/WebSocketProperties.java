package io.choerodon.notify.websocket.ws;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("choerodon.ws")
public class WebSocketProperties {

    private String[] paths = {"/choerodon:msg/**"};

    private boolean oauth = false;

    private String oauthUrl = "http://oauth-server/oauth/api/user";

    public String[] getPaths() {
        return paths;
    }

    public void setPaths(String[] paths) {
        this.paths = paths;
    }

    public boolean isOauth() {
        return oauth;
    }

    public void setOauth(boolean oauth) {
        this.oauth = oauth;
    }

    public String getOauthUrl() {
        return oauthUrl;
    }

    public void setOauthUrl(String oauthUrl) {
        this.oauthUrl = oauthUrl;
    }
}
