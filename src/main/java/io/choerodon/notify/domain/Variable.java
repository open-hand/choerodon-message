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

@Getter
@Setter
@ToString
@ModifyAudit
@VersionAudit
@Table(name = "notify_variable")
@NoArgsConstructor
public class Variable extends AuditDomain {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String value;

}
