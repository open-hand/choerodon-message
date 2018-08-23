package io.choerodon.notify.api.service.impl;

import io.choerodon.notify.domain.Record;
import org.springframework.stereotype.Component;
import rx.Observable;
import rx.subjects.PublishSubject;

import java.util.ArrayList;
import java.util.List;

@Component
public class EmailQueueObservable extends Observable<Record> {

    private static PublishSubject<Record> publishSubject = PublishSubject.create();
    private static List<Record> valuesCache = new ArrayList<>();

    public EmailQueueObservable() {
        super(subscriber -> {
            Observable.from(valuesCache)
                    .doOnNext(subscriber::onNext)
                    .doOnCompleted(valuesCache::clear)
                    .subscribe();

            publishSubject.subscribe(subscriber);
        });
    }

    public void emit(Record value) {
        if (publishSubject.hasObservers()) {
            publishSubject.onNext(value);
        } else {
            valuesCache.add(value);
        }
    }


}
