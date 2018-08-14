package io.choerodon.notify.api.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SendSettingUpdateDTO {

    private Long id;

    private Long emailTemplateId;

    private Long smsTemplateId;

    private Long pmTemplateId;

    private Integer retryCount;

    private Boolean isSendInstantly;

    private Boolean isManualRetry;

    @NotNull(message = "error.sendSetting.objectVersionNumberNull")
    private Long objectVersionNumber;

}
