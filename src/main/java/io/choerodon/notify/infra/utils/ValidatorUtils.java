package io.choerodon.notify.infra.utils;

import io.choerodon.notify.infra.config.NotifyProperties;
import org.springframework.util.StringUtils;

public class ValidatorUtils {

    private ValidatorUtils() {
    }

    public static boolean valid(NotifyProperties.Email mail) {
        return validNotEmpty(mail.getAccount(), mail.getPassword(), mail.getHost(),
                mail.getPort(), mail.getProtocol(), mail.getSsl());
    }

    private static boolean validNotEmpty(Object... args) {
        for (Object arg : args) {
            if (arg == null) {
                return false;
            }
            if (arg instanceof String && StringUtils.isEmpty(arg)) {
                return false;
            }
        }
        return true;
    }
}
