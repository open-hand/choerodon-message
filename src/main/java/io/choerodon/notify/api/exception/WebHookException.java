package io.choerodon.notify.api.exception;

public class WebHookException extends RuntimeException {
    public WebHookException(String message) {
        super(message);
    }
}
