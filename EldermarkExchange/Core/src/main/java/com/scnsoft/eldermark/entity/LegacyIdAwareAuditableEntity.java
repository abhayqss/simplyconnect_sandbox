package com.scnsoft.eldermark.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class LegacyIdAwareAuditableEntity extends BasicEntity {
    @Column(name = "legacy_id", nullable = false)
    private long legacyId;

    public long getLegacyId() {
        return legacyId;
    }

    public void setLegacyId(long legacyId) {
        this.legacyId = legacyId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[id=").append(getId()).append(", legacyId=").append(legacyId);
        if (getDatabase() != null) {
            sb.append(", databaseId=").append(getDatabase().getId());
        }
        sb.append("]");
        return sb.toString();
    }
}
