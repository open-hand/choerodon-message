package io.choerodon.notify.domain;

import io.choerodon.notify.api.pojo.RecordSendData;
import io.choerodon.notify.infra.dto.MailingRecordDTO;


public class Record extends MailingRecordDTO {

    private RecordSendData sendData;

    @Override
    public String toString() {
        return "Record{" +
                "id=" + this.getId() +
                ", status='" + this.getStatus() + '\'' +
                ", receiveAccount='" + this.getReceiveAccount() + '\'' +
                ", failedReason='" + this.getFailedReason() + '\'' +
                ", sendSettingCode='" + this.getSendSettingCode() + '\'' +
                ", retryCount=" + this.getRetryCount() +
                ", variables='" + this.getVariables() + '\'' +
                ", templateId=" + this.getTemplateId() +
                ", sendData=" + sendData +
                '}';
    }

    public RecordSendData getSendData() {
        return sendData;
    }

    public void setSendData(RecordSendData sendData) {
        this.sendData = sendData;
    }
}
