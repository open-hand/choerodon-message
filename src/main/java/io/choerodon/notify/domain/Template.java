package io.choerodon.notify.domain;


import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.Getter;
import lombok.Setter;

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
@Table(name = "notify_template")
public class Template extends AuditDomain {

    @Id
    @GeneratedValue
    private Long id;

    private String code;

    private String name;

    private String messageType;

    private Boolean isPredefined;

    private Long businessTypeId;

    private String emailTitle;

    private String emailContent;

    private String smsContent;

}
