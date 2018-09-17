package io.choerodon.notify.websocket.ws;

import java.util.Objects;

public class WebSocketSendPayload<T> {

    public static final String MSG_TYPE_SESSION = "session";

    private String type;

    private String key;

    private String uuid;

    private T data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public WebSocketSendPayload() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public WebSocketSendPayload(String type, String key, T data) {
        this.type = type;
        this.key = key;
        this.data = data;
        this.uuid = WebSocketUtils.generateUUID();
    }

    @Override
    public String toString() {
        return "WebSocketSendPayload{" +
                "type='" + type + '\'' +
                ", key='" + key + '\'' +
                ", uuid='" + uuid + '\'' +
                ", data=" + data +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebSocketSendPayload<?> that = (WebSocketSendPayload<?>) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {

        return Objects.hash(uuid);
    }
}
