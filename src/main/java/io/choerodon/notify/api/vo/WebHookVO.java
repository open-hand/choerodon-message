package io.choerodon.notify.api.vo;

import io.choerodon.notify.infra.dto.SendSettingCategoryDTO;
import io.choerodon.notify.infra.dto.SendSettingDTO;
import io.choerodon.notify.infra.dto.WebHookDTO;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

/**
 * @author bgzyy
 * @since 2019/9/11
 */
public class WebHookVO extends WebHookDTO {

    @ApiModelProperty("可选发送设置主键集合")
    private SendSetting triggerEventSelection;

    @NotEmpty(message = "error.web.hook.create.or.update.send.setting.ids.can.not.be.empty")
    @ApiModelProperty("已选发送设置主键集合")
    private Set<Long> sendSettingIdList;

    public Set<Long> getSendSettingIdList() {
        return sendSettingIdList;
    }

    public WebHookVO setSendSettingIdList(Set<Long> sendSettingIdList) {
        this.sendSettingIdList = sendSettingIdList;
        return this;
    }

    public SendSetting getTriggerEventSelection() {
        return triggerEventSelection;
    }

    public WebHookVO setTriggerEventSelection(SendSetting triggerEventSelection) {
        this.triggerEventSelection = triggerEventSelection;
        return this;
    }

    public static class SendSetting {
        @ApiModelProperty("可选发送类型集合")
        private Set<SendSettingCategoryDTO> sendSettingCategorySelection;
        @ApiModelProperty("可选发送设置集合")
        private Set<SendSettingDTO> sendSettingSelection;

        public Set<SendSettingCategoryDTO> getSendSettingCategorySelection() {
            return sendSettingCategorySelection;
        }

        public SendSetting setSendSettingCategorySelection(Set<SendSettingCategoryDTO> sendSettingCategorySelection) {
            this.sendSettingCategorySelection = sendSettingCategorySelection;
            return this;
        }

        public Set<SendSettingDTO> getSendSettingSelection() {
            return sendSettingSelection;
        }

        public SendSetting setSendSettingSelection(Set<SendSettingDTO> sendSettingSelection) {
            this.sendSettingSelection = sendSettingSelection;
            return this;
        }
    }
}