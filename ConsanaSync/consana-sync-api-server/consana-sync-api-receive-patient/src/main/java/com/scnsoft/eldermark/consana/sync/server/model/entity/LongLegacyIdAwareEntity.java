package com.scnsoft.eldermark.consana.sync.server.model.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class LongLegacyIdAwareEntity extends BasicEntity implements LegacyIdAwareEntity<Long> {
    @Column(name = "legacy_id", nullable = false)
    private Long legacyId;

    @Override
    public Long getLegacyId() {
        return legacyId;
    }

    @Override
    public void setLegacyId(Long legacyId) {
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
