package io.choerodon.message.api.vo;

public class SendSettingDetailTreeVO extends SendSettingDetailVO {
    private Long parentId;
    private Long sequenceId;

    public Long getParentId() {
        return parentId;
    }

    public SendSettingDetailTreeVO setParentId(Long parentId) {
        this.parentId = parentId;
        return this;
    }

    public Long getSequenceId() {
        return sequenceId;
    }

    public SendSettingDetailTreeVO setSequenceId(Long sequenceId) {
        this.sequenceId = sequenceId;
        return this;
    }
}
