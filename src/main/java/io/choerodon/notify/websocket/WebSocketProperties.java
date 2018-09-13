package io.choerodon.notify.websocket;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("choerodon.ws")
public class WebSocketProperties {

    private String[] paths = {"/choerodon_msg/**"};

    public String[] getPaths() {
        return paths;
    }

    public void setPaths(String[] paths) {
        this.paths = paths;
    }
}
