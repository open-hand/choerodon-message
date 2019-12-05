package io.choerodon.notify.api.dto;

public class SendSettingDetailTreeDTO extends SendSettingDetailDTO {
    private Long parentId;
    private Long sequenceId;

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(Long sequenceId) {
        this.sequenceId = sequenceId;
    }
}
