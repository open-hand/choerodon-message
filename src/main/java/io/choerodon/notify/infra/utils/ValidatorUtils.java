package io.choerodon.notify.infra.utils;

import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.util.StringUtils;

public class ValidatorUtils {

    private ValidatorUtils() {
    }

    public static boolean valid(MailProperties mail) {
        return validNotEmpty(mail.getUsername(), mail.getPassword(), mail.getHost(),
                mail.getPort(), mail.getProtocol(), false);
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
