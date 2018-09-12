package io.choerodon.notify.infra.websocket;

import io.choerodon.websocket.Msg;
import io.choerodon.websocket.process.AbstractAgentMsgHandler;
import org.springframework.stereotype.Component;

@Component
public class NotifyAgentMsgHandler extends AbstractAgentMsgHandler {

    @Override
    public void process(Msg msg) {

    }

    @Override
    public int getOrder() {
        return 0;
    }
}
