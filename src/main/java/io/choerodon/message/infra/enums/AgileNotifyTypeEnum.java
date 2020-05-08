package io.choerodon.message.infra.enums;

import java.util.HashMap;
import java.util.Map;

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

    public static Map<String, Integer> orderMapping = new HashMap(3);

    static {
        orderMapping.put(ISSUE_CREATE.value(), 10);
        orderMapping.put(ISSUE_ASSIGNEE.value(), 20);
        orderMapping.put(ISSUE_SOLVE.value(), 30);
    }
}
