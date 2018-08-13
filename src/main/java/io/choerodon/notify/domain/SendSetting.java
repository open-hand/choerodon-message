package io.choerodon.notify.domain;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 消息业务类型
 */
@Getter
@Setter
@ModifyAudit
@VersionAudit
@Table(name = "notify_send_setting")
@NoArgsConstructor
public class SendSetting extends AuditDomain {

    @Id
    @GeneratedValue
    private Long id;

    private String code;

    private String name;

    private String description;

    private Long emailTemplateId;

    private Long smsTemplateId;

    private Long pmTemplateId;

    private String level;

    private Integer retryCount;

    private Boolean isSendInstantly;

    private Boolean isManualRetry;


    public SendSetting(String code) {
        this.code = code;
    }

    public SendSetting(String code, String name, String description, String level) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.level = level;
    }
}
