package com.scnsoft.eldermark.dump.entity;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class ClientAwareAuditableEntity extends AuditableEntity {

    @JoinColumn(name = "resident_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Client client;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
