package io.choerodon.message.api.vo;

/**
 * Created by wangxiang on 2021/11/10
 */
public class MessageTrxStatusVO {
    private Long messageId;

    private String receiverAddress;

    private String trxStatusCode;

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getTrxStatusCode() {
        return trxStatusCode;
    }

    public void setTrxStatusCode(String trxStatusCode) {
        this.trxStatusCode = trxStatusCode;
    }
}
