package io.choerodon.message.api.vo;

import java.util.List;
import java.util.Set;
import javax.validation.constraints.NotEmpty;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.message.domain.entity.TemplateServer;
import org.hzero.message.domain.entity.WebhookServer;


/**
 * @author bgzyy
 * @since 2019/9/11
 */
public class WebHookVO extends WebhookServer {


    @NotEmpty(message = "error.web.hook.create.or.update.send.setting.ids.can.not.be.empty")
    @ApiModelProperty("已选发送设置主键集合")
    private Set<Long> sendSettingIdList;

    @ApiModelProperty("消息类型")
    private List<TemplateServer> templateServers;

    public Set<Long> getSendSettingIdList() {
        return sendSettingIdList;
    }

    public WebHookVO setSendSettingIdList(Set<Long> sendSettingIdList) {
        this.sendSettingIdList = sendSettingIdList;
        return this;
    }

    public List<TemplateServer> getTemplateServers() {
        return templateServers;
    }

    public void setTemplateServers(List<TemplateServer> templateServers) {
        this.templateServers = templateServers;
    }
}