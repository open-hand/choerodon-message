package io.choerodon.notify.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class RecordListDTO {

    private String status;
    private String email;
    private String templateType;
    private String failedReason;
    private Date creationDate;

}
