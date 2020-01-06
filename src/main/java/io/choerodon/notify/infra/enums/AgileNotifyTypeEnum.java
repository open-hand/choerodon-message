package io.choerodon.notify.infra.enums;

/**
 * 〈功能简述〉
 * 〈devops消息枚举〉
 *
 * @author wanghao
 * @Date 2019/12/17 20:41
 */
public enum AgileNotifyTypeEnum {
    ISSUE_ASSIGNEE("issueAssignee"),
    ISSUE_SOLVE("issueSolve"),
    ISSUE_CREATE("issueCreate");
    private final String value;

    AgileNotifyTypeEnum(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
