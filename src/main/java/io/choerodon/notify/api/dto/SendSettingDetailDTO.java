package io.choerodon.notify.api.dto;

import lombok.Data;

@Data
public class SendSettingDetailDTO {

    private Long id;

    private String code;

    private String name;

    private String description;

    private String emailTemplateCode;

    private String smsTemplateCode;

    private String pmTemplateCode;

    private String level;

    private Integer retryCount;

    private Boolean isSendInstantly;

    private Boolean isManualRetry;

    private Long objectVersionNumber;

}
