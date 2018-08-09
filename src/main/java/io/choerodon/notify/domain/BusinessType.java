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
@Table(name = "notify_business_type")
@NoArgsConstructor
public class BusinessType extends AuditDomain {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String description;

    public BusinessType(String name) {
        this.name = name;
    }

    public BusinessType(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
