package io.choerodon.message.api.vo;

import org.hzero.starter.keyencrypt.core.Encrypt;

public class SendSettingDetailTreeVO extends SendSettingDetailVO {
    @Encrypt
    private Long parentId;
    @Encrypt
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
