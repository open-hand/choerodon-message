package io.choerodon.notify.websocket.ws;

public class WebSocketSendPayload<T> {

    public static final String MSG_TYPE_SESSION = "session";

    private String type;

    private String key;

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
    }

    @Override
    public String toString() {
        return "WebSocketSendPayload{" +
                "type='" + type + '\'' +
                ", key='" + key + '\'' +
                ", data=" + data +
                '}';
    }


}
