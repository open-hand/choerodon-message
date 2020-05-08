package io.choerodon.message.api.vo;

import java.util.Objects;

/**
 * @author dengyouquan
 */
public class UserVO {

    private Long id;

    private String loginName;

    private String realName;

    private String imageUrl;

    private String email;

    private String phone;

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

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserVO userDTO = (UserVO) o;
        return Objects.equals(id, userDTO.id) &&
                Objects.equals(loginName, userDTO.loginName) &&
                Objects.equals(realName, userDTO.realName) &&
                Objects.equals(imageUrl, userDTO.imageUrl) &&
                Objects.equals(email, userDTO.email) &&
                Objects.equals(phone, userDTO.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, loginName, realName, imageUrl, email, phone);
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", loginName='" + loginName + '\'' +
                ", realName='" + realName + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
