package io.choerodon.message.infra.dto;

import org.hzero.message.domain.entity.TemplateServer;

public class TemplateServerC7NDTO extends TemplateServer {
    private boolean emailEnabledFlag;
    private boolean smsEnabledFlag;
    private boolean pmEnabledFlag;

    public boolean getEmailEnabledFlag() {
        return emailEnabledFlag;
    }

    public void setEmailEnabledFlag(boolean emailEnabledFlag) {
        this.emailEnabledFlag = emailEnabledFlag;
    }

    public boolean getSmsEnabledFlag() {
        return smsEnabledFlag;
    }

    public void setSmsEnabledFlag(boolean smsEnabledFlag) {
        this.smsEnabledFlag = smsEnabledFlag;
    }

    public boolean getPmEnabledFlag() {
        return pmEnabledFlag;
    }

    public void setPmEnabledFlag(boolean pmEnabledFlag) {
        this.pmEnabledFlag = pmEnabledFlag;
    }
}
