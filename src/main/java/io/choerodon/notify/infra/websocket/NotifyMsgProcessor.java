package io.choerodon.notify.infra.websocket;

import io.choerodon.websocket.Msg;
import io.choerodon.websocket.process.MsgProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NotifyMsgProcessor implements MsgProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotifyMsgProcessor.class);

    @Override
    public boolean shouldProcess(Msg msg) {
        return true;
    }

    @Override
    public void process(Msg msg) {
       LOGGER.info("message {}", msg);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
