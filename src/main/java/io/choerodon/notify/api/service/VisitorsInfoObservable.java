package io.choerodon.notify.api.service;

import io.choerodon.notify.api.pojo.VisitorsInfo;
import org.springframework.stereotype.Component;

import java.util.Observable;

@Component
public class VisitorsInfoObservable extends Observable {

    public void sendEvent(VisitorsInfo info) {
        setChanged();
        notifyObservers(info);
    }

}
