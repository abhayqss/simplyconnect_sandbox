package com.scnsoft.eldermark.entity.audit;

import javax.persistence.*;

@Entity
@Table(name = "AuditLog_FirstResident")
public class AuditLogFirstClient {

    @Id
    @Column(name = "audit_log_id", nullable = false)
    private Long auditLogId;

    @JoinColumn(name = "audit_log_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private AuditLog auditLog;

    @Column(name = "client_name")
    private String clientName;

    public Long getAuditLogId() {
        return auditLogId;
    }

    public void setAuditLogId(Long auditLogId) {
        this.auditLogId = auditLogId;
    }

    public AuditLog getAuditLog() {
        return auditLog;
    }

    public void setAuditLog(AuditLog auditLog) {
        this.auditLog = auditLog;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
}