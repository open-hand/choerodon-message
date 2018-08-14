package io.choerodon.notify.api.dto;


import lombok.Data;

@Data
public class SendSettingListDTO {

    private Long id;

    private String code;

    private String name;

    private String description;

    private String emailTemplateCode;

    private String smsTemplateCode;

    private String pmTemplateCode;

}
