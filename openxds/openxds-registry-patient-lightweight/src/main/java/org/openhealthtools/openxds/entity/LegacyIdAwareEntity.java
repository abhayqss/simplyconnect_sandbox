package org.openhealthtools.openxds.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class LegacyIdAwareEntity extends BasicEntity {
    @Column(name = "legacy_id", nullable = false)
    private long legacyId;

    public long getLegacyId() {
        return legacyId;
    }

    public void setLegacyId(long legacyId) {
        this.legacyId = legacyId;
    }
}
