package io.choerodon.notify.api.dto;


import javax.validation.constraints.NotEmpty;

public class NoticeSendDTO extends io.choerodon.core.notify.NoticeSendDTO {

    /**
     * 发送的业务类型code
     */
    @NotEmpty(message = "error.postNotify.codeEmpty")
    private String code;

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }
}
