package io.choerodon.notify.domain;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 邮箱服务器，短信服务器的配置
 */
@ModifyAudit
@VersionAudit
@Table(name = "notify_config")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Config extends AuditDomain {

    @Id
    @GeneratedValue
    private Long id;
    private String emailAccount;
    private String emailPassword;
    private String emailSendName;
    private String emailProtocol;
    private String emailHost;
    private Integer emailPort;
    private Boolean emailSsl;
    private String smsDomain;
    private String smsKeyId;
    private String smsKeyPassword;

}
