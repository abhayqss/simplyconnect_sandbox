package com.scnsoft.eldermark.consana.sync.client.model.entities;

import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;

@Immutable
@Entity
@Table(name = "EventType")
public class EventType extends BaseReadOnlyEntity {

    @Basic(optional = false)
    @Column(name = "is_service", nullable = false)
    private boolean service;

    public boolean isService() {
        return service;
    }

    public void setService(boolean service) {
        this.service = service;
    }
}
