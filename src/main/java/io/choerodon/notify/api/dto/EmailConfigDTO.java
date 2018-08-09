package io.choerodon.notify.api.dto;

import io.choerodon.notify.domain.Config;
import io.choerodon.notify.infra.utils.ConvertUtils;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import org.modelmapper.PropertyMap;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class EmailConfigDTO {

    @NotEmpty(message = "error.email.accountEmpty")
    private String account;

    @NotEmpty(message = "error.email.passwordEmpty")
    private String password;

    private String sendName;

    @NotEmpty(message = "error.email.protocolEmpty")
    private String protocol;

    @NotEmpty(message = "error.email.hostEmpty")
    private String host;

    @NotNull(message = "error.email.portNull")
    @Min(value = 0, message = "error.email.portScope")
    @Max(value = 65535, message = "error.email.portScope")
    private Integer port;

    private Boolean ssl;

    public static PropertyMap<EmailConfigDTO, Config> dto2Entity() {
        return new PropertyMap<EmailConfigDTO, Config>() {
            @Override
            protected void configure() {
                using(ConvertUtils.addPrefix("email")).map();
            }
        };
    }

    public static PropertyMap<Config, EmailConfigDTO> entity2Dto() {
        return new PropertyMap<Config, EmailConfigDTO>() {
            @Override
            protected void configure() {
                using(ConvertUtils.removePrefix("email")).map();
            }
        };
    }

}
