package com.scnsoft.eldermark.entity.document.ccd;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.basic.LegacyIdAwareEntity;
import com.scnsoft.eldermark.entity.community.Community;

@Entity
@Table(name = "Custodian")
public class Custodian extends LegacyIdAwareEntity {
    private static final long serialVersionUID = 1L;

    @OneToOne
    @JoinColumn (name = "organization_id")
    private Community community;

    @OneToOne (mappedBy = "custodian")
    private Client client;

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
