package com.scnsoft.eldermark.consana.sync.server.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
@Data
public abstract class LongLegacyTableAwareEntity extends LongLegacyIdAwareEntity implements LegacyTableAwareEntity {

    @Column(name = "legacy_table", nullable = false)
    private String legacyTable;
}
