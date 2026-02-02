package com.scnsoft.eldermark.entity.basic;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class StringLegacyTableAwareEntity extends StringLegacyIdAwareEntity implements LegacyTableAware {
   
    private static final long serialVersionUID = 1L;
    
    @Column(name = "legacy_table")
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
