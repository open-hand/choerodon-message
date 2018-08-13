package io.choerodon.notify.domain;


import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 消息模板实体
 * 包括邮件模板和短信模版
 */
@Getter
@Setter
@ModifyAudit
@VersionAudit
@ToString
@Table(name = "notify_template")
public class Template extends AuditDomain {

    public static final String MSG_TYPE_EMAIL = "email";
    public static final String MSG_TYPE_SMS = "sms";

    @Id
    @GeneratedValue
    private Long id;

    private String code;

    private String name;

    private String messageType;

    private Boolean isPredefined;

    private String businessType;

    private String emailTitle;

    private String emailContent;

    private String smsContent;

}
