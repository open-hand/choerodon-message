package io.choerodon.notify.api.dto;

import lombok.Data;

import java.util.Map;

@Data
public class EmailSendDTO {


    private String code;

    private String emailAddress;

    private Map<String, Object> variables;

}
