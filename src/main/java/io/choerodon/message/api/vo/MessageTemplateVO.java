package io.choerodon.message.api.vo;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.message.domain.entity.MessageTemplate;

/**
 * @author scp
 * @date 2020/5/23
 * @description
 */
public class MessageTemplateVO extends MessageTemplate {
    @ApiModelProperty(value = "发送类型")
    private String sendingType;

    public String getSendingType() {
        return sendingType;
    }

    public void setSendingType(String sendingType) {
        this.sendingType = sendingType;
    }
}
