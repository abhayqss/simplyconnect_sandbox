package com.scnsoft.eldermark.consana.sync.client.model.entities;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseReadOnlyEntity {

    @Id
    @Column(insertable = false, updatable = false, nullable = false)
    private Long id;

    BaseReadOnlyEntity() {
    }

    BaseReadOnlyEntity(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
