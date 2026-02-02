package com.scnsoft.eldermark.entity.basic;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class LegacyTableAwareEntity extends LegacyIdAwareEntity implements LegacyTableAware {
    private static final long serialVersionUID = 1L;

    @Column(name = "legacy_table", nullable = false)
    private String legacyTable;

    public String getLegacyTable() {
        return legacyTable;
    }

    public void setLegacyTable(String legacyTable) {
        this.legacyTable = legacyTable;
    }
}
