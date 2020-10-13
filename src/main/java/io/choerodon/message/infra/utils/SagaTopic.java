package io.choerodon.message.infra.utils;

/**
 * Created by wangxiang on 2020/10/13
 */
public final class SagaTopic {
    private SagaTopic(){

    }
    //删除用户角色
    public static final String MEMBER_ROLE_DELETE = "iam-delete-memberRole";

    public static final String MESSAGE_DELETE_PROJECT_USER = "message-delete-project-user";
}
