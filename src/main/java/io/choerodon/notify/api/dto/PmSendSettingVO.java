package io.choerodon.notify.api.dto;

import io.choerodon.notify.infra.dto.SendSettingDTO;
import io.swagger.annotations.ApiModelProperty;

/**
 * 该VO用于站内信内容设置的发送设置(对应库表 notify_send_setting {@link SendSettingDTO})
 *
 * @author Eugen
 */
public class PmSendSettingVO {

    @ApiModelProperty(value = "发送设置主键")
    private Long id;

    @ApiModelProperty(value = "站内信模版主键")
    private Long pmTemplateId;

    @ApiModelProperty(value = "站内信模版名称")
    private String pmTemplateName;

    @ApiModelProperty(value = "站内信模版标题")
    private String pmTemplateTitle;

    @ApiModelProperty(value = "站内信消息类型")
    private String pmType;

    @ApiModelProperty(value = "乐观所版本号")
    private Long objectVersionNumber;

    public Long getId() {
        return id;
    }

    public PmSendSettingVO setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getPmTemplateId() {
        return pmTemplateId;
    }

    public PmSendSettingVO setPmTemplateId(Long pmTemplateId) {
        this.pmTemplateId = pmTemplateId;
        return this;
    }

    public String getPmTemplateName() {
        return pmTemplateName;
    }

    public PmSendSettingVO setPmTemplateName(String pmTemplateName) {
        this.pmTemplateName = pmTemplateName;
        return this;
    }

    public String getPmTemplateTitle() {
        return pmTemplateTitle;
    }

    public PmSendSettingVO setPmTemplateTitle(String pmTemplateTitle) {
        this.pmTemplateTitle = pmTemplateTitle;
        return this;
    }

    public String getPmType() {
        return pmType;
    }

    public PmSendSettingVO setPmType(String pmType) {
        this.pmType = pmType;
        return this;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public PmSendSettingVO setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
        return this;
    }
}
