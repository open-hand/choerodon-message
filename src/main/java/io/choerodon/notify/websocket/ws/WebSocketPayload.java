package io.choerodon.notify.websocket.ws;

import java.util.Objects;

public class WebSocketPayload<T> {

    public static final String MSG_TYPE_SESSION = "session";

    private String type;

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

    public WebSocketPayload(String type, T data) {
        this.type = type;
        this.data = data;
        this.uuid = WebSocketUtils.generateUUID();
    }

    @Override
    public String toString() {
        return "WebSocketPayload{" +
                "type='" + type + '\'' +
                ", uuid='" + uuid + '\'' +
                ", data=" + data +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebSocketPayload<?> webSocketPayload = (WebSocketPayload<?>) o;
        return Objects.equals(uuid, webSocketPayload.uuid);
    }

    @Override
    public int hashCode() {

        return Objects.hash(uuid);
    }
}
