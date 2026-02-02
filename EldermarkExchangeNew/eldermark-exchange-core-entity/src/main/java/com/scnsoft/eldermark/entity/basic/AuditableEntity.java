package com.scnsoft.eldermark.entity.basic;

import javax.persistence.*;
import java.time.Instant;

@MappedSuperclass
public abstract class AuditableEntity extends HistoryIdsAwareEntity {

    @Column(name = "archived", nullable = false)
    private Boolean archived;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AuditableEntityStatus auditableStatus;

    @Column(name = "last_modified_date", nullable = false)
    private Instant lastModifiedDate;

    public AuditableEntity() {
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public AuditableEntityStatus getAuditableStatus() {
        return auditableStatus;
    }

    public void setAuditableStatus(AuditableEntityStatus status) {
        this.auditableStatus = status;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
