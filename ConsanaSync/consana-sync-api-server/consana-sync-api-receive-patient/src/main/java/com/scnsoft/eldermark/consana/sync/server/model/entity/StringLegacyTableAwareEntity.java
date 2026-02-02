package com.scnsoft.eldermark.consana.sync.server.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Data
@NoArgsConstructor
public abstract class StringLegacyTableAwareEntity extends StringLegacyIdAwareEntity implements LegacyTableAwareEntity {
    @Column(name = "legacy_table", nullable = false)
    private String legacyTable;

    public StringLegacyTableAwareEntity(Long id) {
        super(id);
    }

}
