package io.choerodon.notify.api.exception;

import io.choerodon.notify.api.pojo.EmailSendError;

public class EmailSendException extends RuntimeException {

    private final  EmailSendError error;

    public EmailSendException(Throwable cause, EmailSendError error) {
        super("Send email error", cause);
        this.error = error;
    }

    public EmailSendError getError() {
        return error;
    }

}
