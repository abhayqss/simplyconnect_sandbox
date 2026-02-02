package com.scnsoft.eldermark.consana.sync.client.model.entities;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class StringLegacyTableAwareEntity extends StringLegacyIdAwareEntity implements LegacyTableAwareEntity {

    @Column(name = "legacy_table", nullable = false)
    private String legacyTable;

    public StringLegacyTableAwareEntity() {
    }

    public StringLegacyTableAwareEntity(Long id) {
        super(id);
    }

    @Override
    public String getLegacyTable() {
        return legacyTable;
    }

    @Override
    public void setLegacyTable(String legacyTable) {
        this.legacyTable = legacyTable;
    }
}
