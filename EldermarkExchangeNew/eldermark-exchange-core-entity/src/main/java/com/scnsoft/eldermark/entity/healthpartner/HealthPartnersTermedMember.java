package com.scnsoft.eldermark.entity.healthpartner;

import com.scnsoft.eldermark.entity.Client;

import javax.persistence.*;

@Entity
@Table(name = "HealthPartnersTermedMember")
public class HealthPartnersTermedMember extends BaseHealthPartnersRecord {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", referencedColumnName = "id")
    private Client client;

    @Column(name = "resident_id", insertable = false, updatable = false)
    private Long clientId;

    @Column(name = "resident_is_new")
    private Boolean clientIsNew;

    @Transient
    private Exception processingException;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Boolean getClientIsNew() {
        return clientIsNew;
    }

    public void setClientIsNew(Boolean clientExisted) {
        this.clientIsNew = clientExisted;
    }

    public Exception getProcessingException() {
        return processingException;
    }

    public void setProcessingException(Exception processingException) {
        this.processingException = processingException;
    }
}
