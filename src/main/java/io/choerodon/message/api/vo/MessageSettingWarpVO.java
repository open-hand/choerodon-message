package io.choerodon.message.api.vo;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;

/**
 * 〈功能简述〉
 * 〈消息通知设置包装VO〉
 *
 * @author wanghao
 * @Date 2019/12/11 11:09
 */
public class MessageSettingWarpVO {

    @ApiModelProperty("通知事件组集合")
    private List<NotifyEventGroupVO> notifyEventGroupList = new ArrayList<>();
    @ApiModelProperty("通知事件配置集合")
    private List<CustomMessageSettingVO> customMessageSettingList = new ArrayList<>();

    public List<NotifyEventGroupVO> getNotifyEventGroupList() {
        return notifyEventGroupList;
    }

    public void setNotifyEventGroupList(List<NotifyEventGroupVO> notifyEventGroupList) {
        this.notifyEventGroupList = notifyEventGroupList;
    }

    public List<CustomMessageSettingVO> getCustomMessageSettingList() {
        return customMessageSettingList;
    }

    public void setCustomMessageSettingList(List<CustomMessageSettingVO> customMessageSettingList) {
        this.customMessageSettingList = customMessageSettingList;
    }
}
