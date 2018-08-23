package io.choerodon.notify.domain;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Map;

@Getter
@Setter
@ModifyAudit
@VersionAudit
@Table(name = "notify_record")
@NoArgsConstructor
public class Record extends AuditDomain {

    @Id
    @GeneratedValue
    private Long id;
    private String status;
    private String receiveAccount;
    private String templateType;
    private String failedReason;
    private String messageType;
    private Integer maxRetryCount;
    private Boolean isManualRetry;
    private String level;

    @Transient
    private Template template;
    @Transient
    private Map<String, Object> variables;
    @Transient
    private JavaMailSenderImpl mailSender;
    @Transient
    private Config config;

    public enum RecordStatus {
        RUNNING("RUNNING"),
        COMPLETE("COMPLETED"),
        FAILED("FAILED");
        private String value;

        RecordStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public Record(SendSetting setting, String type) {
        this.status = RecordStatus.RUNNING.getValue();
        this.maxRetryCount = setting.getRetryCount();
        this.isManualRetry = setting.getIsManualRetry();
        this.messageType = type;
        this.level = setting.getLevel();
    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", receiveAccount='" + receiveAccount + '\'' +
                ", templateType='" + templateType + '\'' +
                ", failedReason='" + failedReason + '\'' +
                ", messageType='" + messageType + '\'' +
                ", maxRetryCount=" + maxRetryCount +
                ", isManualRetry=" + isManualRetry +
                '}';
    }
}
