package io.choerodon.notify.infra.enums;

public enum EmailSendError {
    AUTH_ERROR("邮箱账号或者密码错误"),
    ADDRESS_ERROR("无法连接到目标邮箱服务器"),
    TEMPLATE_ERROR("邮件模版渲染异常"),
    MIME_ERROR("创建邮件失败"),
    NETWORK_ERROR("网络等原因造成发送异常"),
    UNKNOWN_ERROR("未知异常"),;

    private String reason;

    EmailSendError(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
