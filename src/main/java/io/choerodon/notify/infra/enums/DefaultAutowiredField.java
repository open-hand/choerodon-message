package io.choerodon.notify.infra.enums;

import io.choerodon.notify.api.dto.UserDTO;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dengyouquan
 **/
public enum DefaultAutowiredField {
    LOGIN_NAME("loginName"),
    USER_NAME("userName");
    private String value;

    public String value() {
        return value;
    }

    DefaultAutowiredField(String value) {
        this.value = value;
    }

    /**
     * 注入默认渲染字段
     *
     * @param params
     * @param user
     * @return
     */
    public static Map<String, Object> autowiredDefaultParams(final Map<String, Object> params, final UserDTO user) {
        Map<String, Object> userParams = new HashMap<>(params);
        userParams.put(DefaultAutowiredField.USER_NAME.value(), user.getRealName());
        userParams.put(DefaultAutowiredField.LOGIN_NAME.value(), user.getLoginName());
        return userParams;
    }
}
