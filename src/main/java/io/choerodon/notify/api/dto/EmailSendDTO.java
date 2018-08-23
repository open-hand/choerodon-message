package io.choerodon.notify.api.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmailSendDTO {

    private String code;

    private String destinationEmail;

    private Map<String, Object> variables;

}
