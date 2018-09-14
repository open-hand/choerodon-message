package io.choerodon.notify.websocket.exception;

public class GetSelfSubChannelsFailedException extends RuntimeException {

    public GetSelfSubChannelsFailedException(Throwable throwable) {
        super("cannot get sub redis channel names", throwable);
    }
}
