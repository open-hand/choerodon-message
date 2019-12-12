package io.choerodon.notify.api.vo;

import java.util.Objects;

/**
 * 〈功能简述〉
 * 〈通知用户VO〉
 *
 * @author wanghao
 * @Date 2019/12/11 13:48
 */
public class TargetUserVO {
    private Long id;
    private Long userId;
    private String realName;
    private String loginName;
    private String type;
    private Long messageSettingId;

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getMessageSettingId() {
        return messageSettingId;
    }

    public void setMessageSettingId(Long messageSettingId) {
        this.messageSettingId = messageSettingId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

}
