package io.choerodon.message.infra.dto;

import io.swagger.annotations.ApiModelProperty;
import javax.persistence.Table;
import org.hzero.message.domain.entity.MessageTemplate;


/**
 * Created by wangxiang on 2022/2/28
 */
@Table(
        name = "hmsg_message_template"
)
public class MessageTemplateDTO extends MessageTemplate {
    @ApiModelProperty("消息模板的外部参数:比如存放华为云模板的通道号")
    private String externalParam;

    public String getExternalParam() {
        return externalParam;
    }

    public void setExternalParam(String externalParam) {
        this.externalParam = externalParam;
    }
}
