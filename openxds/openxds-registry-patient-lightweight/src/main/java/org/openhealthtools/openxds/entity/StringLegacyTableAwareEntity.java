package org.openhealthtools.openxds.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class StringLegacyTableAwareEntity extends StringLegacyIdAwareEntity implements LegacyTableAware {
    @Column(name = "legacy_table", nullable = false)
    private String legacyTable;

    public StringLegacyTableAwareEntity() {
    }

    public StringLegacyTableAwareEntity(Long id) {
        super(id);
    }

    public String getLegacyTable() {
        return legacyTable;
    }

    public void setLegacyTable(String legacyTable) {
        this.legacyTable = legacyTable;
    }
}
