package io.choerodon.notify.api.service.impl;

import io.choerodon.notify.api.service.EmailSendService;
import io.choerodon.notify.domain.Record;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.util.concurrent.Executor;

@Component
public class EmailQueueObserver implements Action1<Record> {

    private final EmailSendService emailSendService;

    public EmailQueueObserver(EmailSendService emailSendService,
                              @Qualifier("asyncSendNoticeExecutor") Executor executor,
                              EmailQueueObservable observable) {
        this.emailSendService = emailSendService;
        observable.subscribeOn(Schedulers.from(executor)).subscribe(this);
    }

    @Override
    public void call(Record dto) {
        emailSendService.sendRecord(dto, false);
    }

}
