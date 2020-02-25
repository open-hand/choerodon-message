package io.choerodon.notify.infra.enums;

/**
 * 〈功能简述〉
 * 〈邮件发送状态enum〉
 *
 * @author wanghao
 * @Date 2020/2/25 11:11
 */
public enum EmailSendStatusEnum {

    COMPLETED("COMPLETED"),
    FAILED("FAILED");
    private String value;

    public String value() {
        return value;
    }

    EmailSendStatusEnum(String value) {
        this.value = value;
    }
}
