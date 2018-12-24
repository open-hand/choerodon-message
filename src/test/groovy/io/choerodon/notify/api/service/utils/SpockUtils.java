package io.choerodon.notify.api.service.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import io.choerodon.core.oauth.CustomUserDetails;

/**
 * @author dengyouquan
 **/
public class SpockUtils {
    public static CustomUserDetails getCustomUserDetails() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        CustomUserDetails customUserDetails = new CustomUserDetails("dengyouquan", "123456", authorities);
        customUserDetails.setOrganizationId(1L);
        customUserDetails.setEmail("dengyouquan@qq.com");
        customUserDetails.setAdmin(true);
        customUserDetails.setTimeZone("CTT");
        customUserDetails.setUserId(1L);
        customUserDetails.setLanguage("zh_CN");
        return customUserDetails;
    }

    public static CustomUserDetails getNotAdminCustomUserDetails() {
        CustomUserDetails customUserDetails = getCustomUserDetails();
        customUserDetails.setAdmin(false);
        return customUserDetails;
    }
}
