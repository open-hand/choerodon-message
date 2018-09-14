package io.choerodon.notify.websocket.exception;

import io.choerodon.notify.websocket.client.ReceiveMsgHandler;

public class MsgHandlerDuplicateMathTypeException extends RuntimeException {

    public MsgHandlerDuplicateMathTypeException(ReceiveMsgHandler msgHandler) {
        super("duplicate matchType, matchType must be unique, matchType: " + msgHandler.matchType());
    }

}
