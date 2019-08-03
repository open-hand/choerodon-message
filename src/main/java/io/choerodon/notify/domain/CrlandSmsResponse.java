package io.choerodon.notify.domain;

/**
 * 调用华润发送短信接口的响应体
 */
public class CrlandSmsResponse {

    private String mobile;

    private String sendText;

    private String sendStatus;

    private Long sendTime;

    private String description;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSendText() {
        return sendText;
    }

    public void setSendText(String sendText) {
        this.sendText = sendText;
    }

    public String getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(String sendStatus) {
        this.sendStatus = sendStatus;
    }

    public Long getSendTime() {
        return sendTime;
    }

    public void setSendTime(Long sendTime) {
        this.sendTime = sendTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public enum SendStatus {

        SUCCESS("success"), FAIL("fail");

        private String status;

        SendStatus(String status) {
            this.status = status;
        }

        public String status() {
            return status;
        }

        public static boolean isSuccess(String value) {
            return SUCCESS.status.equalsIgnoreCase(value);
        }

        public static boolean isFail(String value) {
            return FAIL.status.equalsIgnoreCase(value);
        }
    }
}
