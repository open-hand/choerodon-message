package io.choerodon.notify.domain;

import io.choerodon.notify.api.pojo.RecordSendData;
import io.choerodon.notify.infra.dto.SmsRecordDTO;

/**
 * @author jiameng.cao
 * @date 2019/11/4
 */
public class SmsRecord extends SmsRecordDTO {
    private RecordSendData sendData;

    public RecordSendData getSendData() {
        return sendData;
    }

    public void setSendData(RecordSendData sendData) {
        this.sendData = sendData;
    }
}
