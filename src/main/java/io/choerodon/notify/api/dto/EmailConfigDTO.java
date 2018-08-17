package io.choerodon.notify.api.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;

@Getter
@Setter
public class EmailConfigDTO {

    public static final String EMAIL_REGULAR_EXPRESSION = "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?";

    @NotEmpty(message = "error.emailConfig.accountEmpty")
    @Pattern(regexp = EMAIL_REGULAR_EXPRESSION, message = "error.emailConfig.accountIllegal")
    private String account;

    @NotEmpty(message = "error.emailConfig.passwordEmpty")
    private String password;

    private String sendName;

    private String protocol;

    @NotEmpty(message = "error.emailConfig.hostEmpty")
    private String host;

    private Integer port;

    private Boolean ssl;

    private Long objectVersionNumber;
}
