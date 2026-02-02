package org.openhealthtools.openxds.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class StringLegacyIdAwareEntity extends BasicEntity {
    @Column(name = "legacy_id", nullable = false)
    private String legacyId;

    public StringLegacyIdAwareEntity() {
    }

    public StringLegacyIdAwareEntity(Long id) {
        super(id);
    }

    public String getLegacyId() {
        return legacyId;
    }

    public void setLegacyId(String legacyId) {
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

