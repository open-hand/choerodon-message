package io.choerodon.message.infra.enums;

/**
 * 〈功能简述〉
 * 〈邮件发送状态enum〉
 *
 * @author wanghao
 * @Date 2020/2/25 11:11
 */
public enum EmailSendStatusEnum {

    COMPLETED(1),
    FAILED(0);
    private Integer value;

    public Integer value() {
        return value;
    }

    EmailSendStatusEnum(Integer value) {
        this.value = value;
    }
}
